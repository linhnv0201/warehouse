package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.SalesOrderCreationRequest;
import com.linh.warehouse.dto.response.SalesOrderItemResponse;
import com.linh.warehouse.dto.response.SalesOrderResponse;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SalesOrderService {

    SalesOrderRepository salesOrderRepository;
    WarehouseRepository warehouseRepository;
    CustomerRepository customerRepository;
    UserRepository userRepository;
    InventoryRepository inventoryRepository;
    SalesOrderItemRepository salesOrderItemRepository;
    DeliveryOrderItemRepository deliveryOrderItemRepository;

    @Transactional
    public SalesOrderResponse createSalesOrder(SalesOrderCreationRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        User createdBy = getCurrentUser();

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCode(generateSaleOrderCode());
        salesOrder.setWarehouse(warehouse);
        salesOrder.setCustomer(customer);
        salesOrder.setCreatedBy(createdBy);
        salesOrder.setSaleName(request.getSaleName());
        salesOrder.setStatus("PENDING");
        salesOrder.setCreatedAt(LocalDateTime.now());

        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);

        List<SalesOrderItem> items = request.getItems().stream().map(itemReq -> {
            Inventory inventory = inventoryRepository.findById(itemReq.getInventoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

            SalesOrderItem item = new SalesOrderItem();
            item.setSalesOrder(savedOrder);
            item.setInventory(inventory);
            item.setQuantity(itemReq.getQuantity());
            item.setSaleUnitPrice(itemReq.getSaleUnitPrice());
            return item;
        }).collect(Collectors.toList());

        salesOrderItemRepository.saveAll(items);

        // Tính tổng tiền có VAT
        BigDecimal totalPrice = items.stream()
                .map(i -> {
                    Inventory inv = i.getInventory();
                    BigDecimal unitPrice = i.getSaleUnitPrice();
                    BigDecimal quantity = BigDecimal.valueOf(i.getQuantity());
                    BigDecimal taxRate = inv.getTaxRate() != null ? inv.getTaxRate() : BigDecimal.ZERO;

                    return unitPrice.multiply(quantity)
                            .multiply(BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100))));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        savedOrder.setTotalPrice(totalPrice);
        salesOrderRepository.save(savedOrder);

        return toSalesOrderResponse(savedOrder, items);
    }

    private SalesOrderResponse toSalesOrderResponse(SalesOrder order, List<SalesOrderItem> items) {
        SalesOrderResponse response = new SalesOrderResponse();
        response.setId(order.getId());
        response.setCode(order.getCode());
        response.setWarehouseName(order.getWarehouse().getName());
        response.setCustomerName(order.getCustomer().getName());
        response.setSaleName(order.getSaleName());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setCreatedBy(order.getCreatedBy() != null ? order.getCreatedBy().getFullname() : null);

        if (order.getApprovedBy() != null) {
            response.setApprovedBy(order.getApprovedBy().getFullname());
            response.setApprovedAt(order.getApprovedAt());
        }

        response.setTotalPrice(order.getTotalPrice());

        List<SalesOrderItemResponse> itemResponses = items.stream()
                .map(item -> {
                    Inventory inv = item.getInventory();

                    // 🔍 Lấy số lượng đã xuất
                    int deliveredQty = deliveryOrderItemRepository.getTotalDeliveredQuantity(item.getId());
                    int remaining = item.getQuantity() - deliveredQty;

                    // Tính thành tiền có VAT
                    BigDecimal unitPrice = item.getSaleUnitPrice();
                    BigDecimal taxRate = inv.getTaxRate() != null ? inv.getTaxRate() : BigDecimal.ZERO;
                    BigDecimal totalPrice = unitPrice
                            .multiply(BigDecimal.valueOf(item.getQuantity()))
                            .multiply(BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100))));

                    return SalesOrderItemResponse.builder()
                            .id(item.getId())
                            .productCode(inv.getProductCode())
                            .productName(inv.getProductName())
                            .warehouse(inv.getWarehouse().getName())
                            .quantity(item.getQuantity())
                            .remainingQuantity(remaining)
                            .saleUnitPrice(unitPrice)
                            .taxRate(taxRate)
                            .totalPrice(totalPrice)
                            .build();
                })
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
    }


    public List<SalesOrderResponse> getSalesOrdersByStatus(String status) {
        List<SalesOrder> orders;

        if ("ALL".equalsIgnoreCase(status)) {
            orders = salesOrderRepository.findAll();
        } else {
            orders = salesOrderRepository.findByStatusIgnoreCase(status);
        }

        return orders.stream()
                .map(order -> {
                    List<SalesOrderItem> items = salesOrderItemRepository.findBySalesOrderId(order.getId());
                    return toSalesOrderResponse(order, items);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public SalesOrderResponse approveOrDenySalesOrder(Integer orderId, String newStatus) {
        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.SALES_ORDER_NOT_FOUND));

        if (!"PENDING".equalsIgnoreCase(order.getStatus())) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }

        User currentUser = getCurrentUser();

        if ("APPROVED".equalsIgnoreCase(newStatus)) {
            List<SalesOrderItem> items = salesOrderItemRepository.findBySalesOrderId(orderId);

            for (SalesOrderItem item : items) {
                Inventory inventory = inventoryRepository.findById(item.getInventory().getId())
                        .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));

                int available = inventory.getQuantityAvailable();
                int required = item.getQuantity();

                if (available < required) {
                    throw new AppException(ErrorCode.INSUFFICIENT_INVENTORY,
                            "Sản phẩm ID " + inventory.getId() + " không đủ tồn kho để duyệt đơn.");
                }

                inventory.setQuantityAvailable(available - required);
                inventory.setQuantityReserved(inventory.getQuantityReserved() + required);
                inventoryRepository.save(inventory);
            }

            order.setStatus("APPROVED");
            order.setApprovedBy(currentUser);
            order.setApprovedAt(LocalDateTime.now());

        } else if ("CANCELLED".equalsIgnoreCase(newStatus)) {
            order.setStatus("CANCELLED");
            order.setApprovedBy(null);

        } else {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }

        SalesOrder saved = salesOrderRepository.save(order);
        List<SalesOrderItem> items = salesOrderItemRepository.findBySalesOrderId(saved.getId());
        return toSalesOrderResponse(saved, items);
    }

    public SalesOrderResponse getSalesOrderById(Integer orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.SALES_ORDER_NOT_FOUND));

        List<SalesOrderItem> items = salesOrderItemRepository.findBySalesOrderId(orderId);
        return toSalesOrderResponse(order, items);
    }

    private String generateSaleOrderCode() {
        String prefix = "SO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        long countToday = salesOrderRepository.countByCreatedAtBetween(
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay()
        );
        return prefix + String.format("%03d", countToday + 1);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
