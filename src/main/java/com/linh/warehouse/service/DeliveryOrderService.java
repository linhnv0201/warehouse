package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.DeliveryOrderItemRequest;
import com.linh.warehouse.dto.request.DeliveryOrderRequest;
import com.linh.warehouse.dto.response.DeliveryOrderItemResponse;
import com.linh.warehouse.dto.response.DeliveryOrderResponse;
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
import java.util.stream.Collectors;

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


    @Transactional
    public DeliveryOrderResponse createDeliveryOrder(Integer salesOrderId, DeliveryOrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User createdBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        SalesOrder salesOrder = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new AppException(ErrorCode.SALES_ORDER_NOT_FOUND));

        DeliveryOrder deliveryOrder = new DeliveryOrder();
        deliveryOrder.setCode(generateDeliveryOrderCode());
        deliveryOrder.setCreatedAt(LocalDateTime.now());
        deliveryOrder.setSalesOrder(salesOrder);
        deliveryOrder.setCreatedBy(createdBy);
        deliveryOrder = deliveryOrderRepository.save(deliveryOrder);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (DeliveryOrderItemRequest itemReq : request.getItems()) {
            SalesOrderItem salesItem = salesOrderItemRepository.findById(itemReq.getSaleOrderItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.SALES_ORDER_ITEM_NOT_FOUND));

            int delivered = deliveryOrderItemRepository.getTotalDeliveredQuantity(salesItem.getId());
            int toDeliver = itemReq.getQuantity();
            int orderQty = salesItem.getQuantity();

            if (delivered + toDeliver > orderQty) {
                throw new AppException(ErrorCode.QUANTITY_EXCEEDS_SALES_ORDER,
                        String.format("Tổng số lượng giao (%d) vượt quá số lượng đặt (%d) cho sản phẩm %s",
                                delivered + toDeliver, orderQty, salesItem.getInventory().getProductCode()));
            }

            Inventory inv = salesItem.getInventory();

            if (inv.getQuantity() < toDeliver || inv.getQuantityReserved() < toDeliver) {
                throw new AppException(ErrorCode.INSUFFICIENT_INVENTORY,
                        "Không đủ hàng đã đặt để giao sản phẩm: " + inv.getProductCode());
            }

            // Tạo DeliveryOrderItem
            DeliveryOrderItem dItem = new DeliveryOrderItem();
            dItem.setDeliveryOrder(deliveryOrder);
            dItem.setSalesOrderItem(salesItem);
            dItem.setQuantity(toDeliver);
            deliveryOrderItemRepository.save(dItem);

            // Cập nhật tồn kho (chỉ trừ quantity và reserved, không đụng quantityAvailable)
            inv.setQuantity(inv.getQuantity() - toDeliver);
            inv.setQuantityReserved(inv.getQuantityReserved() - toDeliver);
            inv.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inv);

            // Tính tiền
            BigDecimal price = salesItem.getSaleUnitPrice();
            BigDecimal taxRate = inv.getTaxRate() != null ? inv.getTaxRate() : BigDecimal.ZERO;
            BigDecimal taxMultiplier = BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100)));
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(toDeliver)).multiply(taxMultiplier);
            totalAmount = totalAmount.add(itemTotal);
        }

        // Tạo hóa đơn
        SaleInvoice invoice = new SaleInvoice();
        invoice.setCode(generateSaleInvoiceCode());
        invoice.setDeliveryOrder(deliveryOrder);
        invoice.setCustomer(salesOrder.getCustomer());
        invoice.setTotalAmount(totalAmount);
        invoice.setStatus("UNPAID");
        invoice.setCreatedAt(LocalDateTime.now());
        saleInvoiceRepository.save(invoice);

        updateOrderStatusIfCompleted(salesOrderId);

        return toDeliveryOrderResponse(deliveryOrder);
    }

    private DeliveryOrderResponse toDeliveryOrderResponse(DeliveryOrder deliveryOrder) {
        List<DeliveryOrderItemResponse> items = deliveryOrderItemRepository
                .findByDeliveryOrderId(deliveryOrder.getId())
                .stream()
                .map(it -> {
                    SalesOrderItem si = it.getSalesOrderItem();
                    Inventory inv = si.getInventory();
                    BigDecimal unitPrice = si.getSaleUnitPrice();
                    BigDecimal taxRate = inv.getTaxRate() != null ? inv.getTaxRate() : BigDecimal.ZERO;
                    BigDecimal taxMultiplier = BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100)));
                    BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(it.getQuantity())).multiply(taxMultiplier);

                    return DeliveryOrderItemResponse.builder()
                            .id(it.getId())
                            .productCode(inv.getProductCode())
                            .productName(inv.getProductName())
                            .warehouse(inv.getWarehouse().getName())
                            .quantity(it.getQuantity())
                            .unitPrice(unitPrice)
                            .taxRate(taxRate)
                            .totalPrice(total)
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal totalAmount = items.stream()
                .map(DeliveryOrderItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DeliveryOrderResponse.builder()
                .id(deliveryOrder.getId())
                .code(deliveryOrder.getCode())
                .warehouseName(deliveryOrder.getSalesOrder().getWarehouse().getName())
                .customerName(deliveryOrder.getSalesOrder().getCustomer().getName())
                .createdBy(deliveryOrder.getCreatedBy().getFullname())
                .createdAt(deliveryOrder.getCreatedAt())
                .items(items)
                .totalAmount(totalAmount)
                .build();
    }

    public DeliveryOrderResponse getById(int id) {
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DELIVERY_ORDER_NOT_FOUND));
        return toDeliveryOrderResponse(deliveryOrder);
    }

    public List<DeliveryOrderResponse> getBySalesOrderId(Integer salesOrderId) {
        salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new AppException(ErrorCode.SALES_ORDER_NOT_FOUND));
        return deliveryOrderRepository
                .findBySalesOrderId(salesOrderId)
                .stream()
                .map(this::toDeliveryOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateOrderStatusIfCompleted(Integer salesOrderId) {
        boolean allDone = salesOrderItemRepository.findBySalesOrderId(salesOrderId)
                .stream()
                .allMatch(si -> {
                    int delivered = deliveryOrderItemRepository.getTotalDeliveredQuantity(si.getId());
                    return delivered >= si.getQuantity();
                });
        if (allDone) {
            SalesOrder so = salesOrderRepository.findById(salesOrderId)
                    .orElseThrow(() -> new AppException(ErrorCode.SALES_ORDER_NOT_FOUND));
            if (!"COMPLETED".equals(so.getStatus())) {
                so.setStatus("COMPLETED");
                salesOrderRepository.save(so);
                log.info("Sales order {} marked as COMPLETED", salesOrderId);
            }
        }
    }

    private String generateDeliveryOrderCode() {
        return "DO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateSaleInvoiceCode() {
        return "SI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
