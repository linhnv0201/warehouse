package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.ReceiveOrderItemRequest;
import com.linh.warehouse.dto.request.ReceiveOrderRequest;
import com.linh.warehouse.dto.response.*;
import com.linh.warehouse.entity.*;
import com.linh.warehouse.exception.AppException;
import com.linh.warehouse.exception.ErrorCode;
import com.linh.warehouse.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReceiveOrderService {
    ReceiveOrderRepository receiveOrderRepository;
    ReceiveOrderItemRepository receiveOrderItemRepository;
    PurchaseOrderRepository purchaseOrderRepository;
    PurchaseOrderItemRepository purchaseOrderItemRepository;
    UserRepository userRepository;
    PurchaseInvoiceRepository purchaseInvoiceRepository;
    InventoryRepository inventoryRepository;

    @Transactional
    public ReceiveOrderResponse createReceiveOrder(Integer purchaseOrderId, ReceiveOrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User createdBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new AppException(ErrorCode.PURCHASE_ORDER_NOT_FOUND));

        ReceiveOrder receiveOrder = new ReceiveOrder();
        receiveOrder.setCode(generateReceiveOrderCode());
        receiveOrder.setCreatedAt(LocalDateTime.now());
        receiveOrder.setPurchaseOrder(purchaseOrder);
        receiveOrder.setCreatedBy(createdBy);
        receiveOrder.setShippingCost(request.getShippingCost());
        receiveOrder = receiveOrderRepository.save(receiveOrder);

        log.info("Created ReceiveOrder with code: {}", receiveOrder.getCode());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<ReceiveOrderItemResponse> itemResponses = new ArrayList<>();

        // ✅ Kiểm tra nếu không có mặt hàng nào được gửi lên
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Đơn nhận hàng phải có ít nhất một mặt hàng.");
        }

        for (ReceiveOrderItemRequest itemReq : request.getItems()) {
            PurchaseOrderItem purchaseItem = purchaseOrderItemRepository.findById(itemReq.getPurchaseOrderItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.PURCHASE_ORDER_ITEM_NOT_FOUND));

            int alreadyReceived = receiveOrderItemRepository.getTotalReceivedQuantity(purchaseItem.getId());
            int toReceive = itemReq.getQuantity();
            int remainingQuantity = purchaseItem.getQuantity() - alreadyReceived;

            if (toReceive > remainingQuantity) {
                throw new AppException(ErrorCode.QUANTITY_EXCEEDS_PURCHASE_ORDER,
                        "Số lượng nhập (" + toReceive +
                                ") vượt quá số lượng còn lại chưa nhập (" + remainingQuantity +
                                ") cho sản phẩm " + purchaseItem.getProduct().getName());
            }

            ReceiveOrderItem item = new ReceiveOrderItem();
            item.setReceiveOrder(receiveOrder);
            item.setPurchaseOrderItem(purchaseItem);
            item.setQuantity(toReceive);
            item = receiveOrderItemRepository.save(item);

            itemResponses.add(ReceiveOrderItemResponse.builder()
                    .id(item.getId())
                    .productName(purchaseItem.getProduct().getName())
                    .quantity(toReceive)
                    .build());

            // Cập nhật tồn kho
            Inventory inventory = inventoryRepository
                    .findByWarehouseIdAndProductCode(purchaseOrder.getWarehouse().getId(), purchaseItem.getProduct().getCode())
                    .orElse(null);

            if (inventory == null) {
                inventory = new Inventory();
                inventory.setWarehouse(purchaseOrder.getWarehouse());
                inventory.setProductCode(purchaseItem.getProduct().getCode());
                inventory.setProductName(purchaseItem.getProduct().getName());
                inventory.setDescription(purchaseItem.getProduct().getDescription());
                inventory.setQuantity(toReceive);
                inventory.setQuantityAvailable(toReceive);
                inventory.setQuantityReserved(0);
                inventory.setUnit(purchaseItem.getProduct().getUnit());
                inventory.setUnitPrice(purchaseItem.getUnitPrice());
                inventory.setTaxRate(purchaseItem.getTaxRate());
            } else {
                int oldQty = inventory.getQuantity();
                BigDecimal oldPrice = inventory.getUnitPrice();
                BigDecimal newPrice = purchaseItem.getUnitPrice();
                BigDecimal totalCost = oldPrice.multiply(BigDecimal.valueOf(oldQty))
                        .add(newPrice.multiply(BigDecimal.valueOf(toReceive)));
                int totalQty = oldQty + toReceive;
                BigDecimal avgPrice = totalCost.divide(BigDecimal.valueOf(totalQty), 2, RoundingMode.HALF_UP);

                inventory.setUnitPrice(avgPrice);
                inventory.setQuantity(totalQty);
                inventory.setQuantityAvailable(inventory.getQuantityAvailable() + toReceive);
            }

            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);

            // ✅ Tính tổng tiền từng sản phẩm bao gồm thuế %
            BigDecimal unitPrice = purchaseItem.getUnitPrice();
            BigDecimal taxRate = purchaseItem.getTaxRate() != null ? purchaseItem.getTaxRate() : BigDecimal.ZERO;

            // Chuyển từ phần trăm (%) sang dạng thập phân
            BigDecimal taxMultiplier = BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));

            BigDecimal itemTotal = unitPrice
                    .multiply(BigDecimal.valueOf(toReceive))
                    .multiply(taxMultiplier);

            totalAmount = totalAmount.add(itemTotal);

        }

        // ✅ Cộng thêm shipping cost
        if (receiveOrder.getShippingCost() != null) {
            totalAmount = totalAmount.add(receiveOrder.getShippingCost());
        }

        // Tạo hóa đơn
        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setCode(generatePurchaseInvoiceCode());
        invoice.setReceiveOrder(receiveOrder);
        invoice.setTotalAmount(totalAmount);
        invoice.setStatus("UNPAID");
        invoice.setCreatedAt(LocalDateTime.now());
        purchaseInvoiceRepository.save(invoice);

        log.info("Created PurchaseInvoice with code: {}, totalAmount: {}", invoice.getCode(), totalAmount);

        updateOrderStatusIfCompleted(purchaseOrder.getId());

        return ReceiveOrderResponse.builder()
                .id(receiveOrder.getId())
                .code(receiveOrder.getCode())
                .createdAt(receiveOrder.getCreatedAt())
                .createdBy(createdBy.getFullname())
                .shippingCost(receiveOrder.getShippingCost())
                .totalAmount(totalAmount)
                .items(itemResponses)
                .build();
    }

    public List<ReceiveOrder> getByPurchaseOrderId(Integer purchaseOrderId) {
        return receiveOrderRepository.findByPurchaseOrderId(purchaseOrderId);
    }

    private String generateReceiveOrderCode() {
        return "RO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generatePurchaseInvoiceCode() {
        return "PI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    public ReceiveOrderResponse getById(int id) {
        // Tìm kiếm phiếu nhập kho theo ID
        ReceiveOrder receiveOrder = receiveOrderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECEIVE_ORDER_NOT_FOUND));

// Lấy thông tin đơn hàng liên quan
        PurchaseOrder purchaseOrder = receiveOrder.getPurchaseOrder();
        User createdBy = receiveOrder.getCreatedBy();

// Tạo list các mục hàng trong phiếu nhập kho
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<ReceiveOrderItemResponse> itemResponses = new ArrayList<>();

// Duyệt qua tất cả các mục trong phiếu nhập
        for (ReceiveOrderItem item : receiveOrderItemRepository.findByReceiveOrderId(id)) {
            PurchaseOrderItem purchaseItem = item.getPurchaseOrderItem();
            int receivedQuantity = item.getQuantity();

            // Lấy đơn giá và thuế
            BigDecimal unitPrice = purchaseItem.getUnitPrice() != null ? purchaseItem.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal taxRate = purchaseItem.getTaxRate() != null ? purchaseItem.getTaxRate() : BigDecimal.ZERO;

            // Tính thành tiền = đơn giá * số lượng * (1 + thuế%)
            BigDecimal taxMultiplier = BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(receivedQuantity)).multiply(taxMultiplier);

            // Cộng dồn tổng tiền
            totalAmount = totalAmount.add(totalPrice);

            // Tạo response cho từng item trong phiếu nhập
            itemResponses.add(ReceiveOrderItemResponse.builder()
                    .id(item.getId())
                    .productName(purchaseItem.getProduct().getName())
                    .quantity(receivedQuantity)
                    .unitPrice(unitPrice)
                    .taxRate(taxRate)
                    .totalPrice(totalPrice)
                    .build());
        }

// Cộng thêm phí vận chuyển vào tổng số tiền
        if (receiveOrder.getShippingCost() != null) {
            totalAmount = totalAmount.add(receiveOrder.getShippingCost());
        }

// Tạo response cho ReceiveOrder
        return ReceiveOrderResponse.builder()
                .id(receiveOrder.getId())
                .code(receiveOrder.getCode())
                .createdAt(receiveOrder.getCreatedAt())
                .createdBy(createdBy.getFullname())
                .shippingCost(receiveOrder.getShippingCost())
                .totalAmount(totalAmount)
                .items(itemResponses)
                .build();

    }

    @Transactional
    public void updateOrderStatusIfCompleted(Integer orderId) {
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByPurchaseOrderId(orderId);

        boolean allReceived = items.stream().allMatch(item -> {
            int received = receiveOrderItemRepository.getTotalReceivedQuantity(item.getId());
            return (item.getQuantity() - received) <= 0;
        });

        if (allReceived) {
            PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.PURCHASE_ORDER_NOT_FOUND));

            if (!"COMPLETED".equals(order.getStatus())) {
                order.setStatus("COMPLETED");
                purchaseOrderRepository.save(order);
                log.info("Purchase order {} marked as COMPLETED because all items are fully received.", orderId);
            }
        }
    }
}
