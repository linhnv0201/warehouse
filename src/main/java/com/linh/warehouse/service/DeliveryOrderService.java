package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.DeliveryOrderItemRequest;
import com.linh.warehouse.dto.request.DeliveryOrderRequest;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DeliveryOrderService {
    DeliveryOrderRepository deliveryOrderRepository;
    DeliveryOrderItemRepository deliveryOrderItemRepository;
    SalesOrderRepository salesOrderRepository;
    SalesOrderItemRepository salesOrderItemRepository;
    UserRepository userRepository;
    InventoryRepository inventoryRepository;
    SaleInvoiceRepository saleInvoiceRepository;

    public DeliveryOrder createDeliveryOrder(DeliveryOrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User createdBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Lấy đơn bán
        SalesOrder salesOrder = salesOrderRepository.findById(request.getSaleOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.SALES_ORDER_NOT_FOUND));

        // Tạo Delivery Order
        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setCode(generateDeliveryOrderCode());
        deliveryOrder.setCreatedAt(LocalDateTime.now());
        deliveryOrder.setSalesOrder(salesOrder);
        deliveryOrder.setCreatedBy(createdBy);
        deliveryOrder.setStatus("PENDING");
        deliveryOrder = deliveryOrderRepository.save(deliveryOrder);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (DeliveryOrderItemRequest itemReq : request.getItems()) {
            SalesOrderItem salesOrderItem = salesOrderItemRepository.findById(itemReq.getSaleOrderItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.SALES_ORDER_ITEM_NOT_FOUND));

            int totalDelivered = deliveryOrderItemRepository.getTotalDeliveredQuantity(salesOrderItem.getId());
            int toDeliver = itemReq.getQuantity();
            int orderQty = salesOrderItem.getQuantity();

            if (totalDelivered + toDeliver > orderQty) {
                throw new AppException(ErrorCode.QUANTITY_EXCEEDS_SALES_ORDER,
                        "Tổng số lượng giao (" + (totalDelivered + toDeliver) +
                                ") vượt quá số lượng đặt hàng (" + orderQty +
                                ") cho sản phẩm: " + salesOrderItem.getInventory().getProductCode());
            }

            // Tạo DeliveryOrderItem
            DeliveryOrderItem item = new DeliveryOrderItem();
            item.setDeliveryOrder(deliveryOrder);
            item.setSalesOrderItem(salesOrderItem);
            item.setQuantity(toDeliver);
            deliveryOrderItemRepository.save(item);

            // Trừ kho
            Inventory inventory = salesOrderItem.getInventory();
            if (inventory.getQuantityAvailable() < toDeliver) {
                throw new AppException(ErrorCode.INSUFFICIENT_INVENTORY,
                        "Không đủ hàng trong kho để giao sản phẩm: " + inventory.getProductCode());
            }

            inventory.setQuantity(inventory.getQuantity() - toDeliver);
            inventory.setQuantityAvailable(inventory.getQuantityAvailable() - toDeliver);
            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);

            // Cộng vào tổng tiền hóa đơn
            BigDecimal itemTotal = salesOrderItem.getSaleUnitPrice()
                    .multiply(BigDecimal.valueOf(toDeliver));
            totalAmount = totalAmount.add(itemTotal);
        }

        // Tạo SaleInvoice
        SaleInvoice invoice = new SaleInvoice();
        invoice.setCode(generateSaleInvoiceCode());
        invoice.setDeliveryOrder(deliveryOrder);
        invoice.setCustomer(salesOrder.getCustomer());
        invoice.setTotalAmount(totalAmount);
        invoice.setStatus("UNPAID");
        invoice.setCreatedAt(LocalDateTime.now());
        saleInvoiceRepository.save(invoice);

        updateOrderStatusIfCompleted(deliveryOrder.getId());

        return deliveryOrder;
    }


    private String generateDeliveryOrderCode() {
        return "DO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateSaleInvoiceCode() {
        return "SI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public DeliveryOrder getById(int id) {
        return deliveryOrderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DELIVERY_ORDER_NOT_FOUND));
    }
    @Transactional
    public void updateOrderStatusIfCompleted(Integer deliveryOrderId) {
        // Lấy tất cả SalesOrderItem theo SalesOrder của DeliveryOrder
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(deliveryOrderId)
                .orElseThrow(() -> new AppException(ErrorCode.DELIVERY_ORDER_NOT_FOUND));

        List<SalesOrderItem> items = salesOrderItemRepository.findBySalesOrderId(deliveryOrder.getSalesOrder().getId());

        boolean allDelivered = items.stream().allMatch(item -> {
            Integer delivered = deliveryOrderItemRepository.getTotalDeliveredQuantity(item.getId());
            return (item.getQuantity() - delivered) <= 0;
        });

        if (allDelivered) {
            if (!"COMPLETED".equals(deliveryOrder.getStatus())) {
                deliveryOrder.setStatus("COMPLETED");
                deliveryOrderRepository.save(deliveryOrder);
                log.info("Delivery order {} marked as COMPLETED because all items are fully delivered.", deliveryOrderId);
            }
        }
    }


}
