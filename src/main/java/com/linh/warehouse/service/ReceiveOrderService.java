package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.ReceiveOrderItemRequest;
import com.linh.warehouse.dto.request.ReceiveOrderRequest;
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
    public ReceiveOrder createReceiveOrder(ReceiveOrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User createdBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Lấy đơn mua
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(request.getPurchaseOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.PURCHASE_ORDER_NOT_FOUND));

        // Tạo Receive Order
        ReceiveOrder receiveOrder = new ReceiveOrder();
        receiveOrder.setCode(generateReceiveOrderCode());
        receiveOrder.setCreatedAt(LocalDateTime.now());
        receiveOrder.setPurchaseOrder(purchaseOrder);
        receiveOrder.setCreatedBy(createdBy);
        receiveOrder = receiveOrderRepository.save(receiveOrder);

        log.info("Created ReceiveOrder with code: {}", receiveOrder.getCode());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (ReceiveOrderItemRequest itemReq : request.getItems()) {
            PurchaseOrderItem purchaseItem = purchaseOrderItemRepository.findById(itemReq.getPurchaseOrderItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.PURCHASE_ORDER_ITEM_NOT_FOUND));

            int alreadyReceived = receiveOrderItemRepository.getTotalReceivedQuantity(purchaseItem.getId());
            int toReceive = itemReq.getQuantity();
            int poQuantity = purchaseItem.getQuantity();

            if (alreadyReceived + toReceive > poQuantity) {
                throw new AppException(ErrorCode.QUANTITY_EXCEEDS_PURCHASE_ORDER,
                        "Tổng số lượng nhập (" + (alreadyReceived + toReceive) +
                                ") vượt quá số lượng đặt (" + poQuantity + ") cho sản phẩm " + purchaseItem.getProduct().getName());
            }

            // Tạo item nhận hàng
            ReceiveOrderItem item = new ReceiveOrderItem();
            item.setReceiveOrder(receiveOrder);
            item.setPurchaseOrderItem(purchaseItem);
            item.setQuantity(toReceive);
            receiveOrderItemRepository.save(item);

            // Cập nhật tồn kho (Inventory)
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
                // Đã có tồn kho, cập nhật số lượng và đơn giá trung bình
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

            // Cộng vào tổng tiền hóa đơn
            BigDecimal itemTotal = purchaseItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(toReceive));
            totalAmount = totalAmount.add(itemTotal);
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

        // Cập nhật trạng thái đơn mua nếu đã nhận đủ
        updateOrderStatusIfCompleted(purchaseOrder.getId());

        return receiveOrder;
    }


    private String generateReceiveOrderCode() {
        return "RO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generatePurchaseInvoiceCode() {
        return "PI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public ReceiveOrder getById(int id) {
        return receiveOrderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECEIVE_ORDER_NOT_FOUND));
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
