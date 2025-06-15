package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.PurchaseOrderCreationRequest;
import com.linh.warehouse.dto.request.PurchaseOrderItemRequest;
import com.linh.warehouse.dto.response.PurchaseOrderItemResponse;
import com.linh.warehouse.dto.response.PurchaseOrderResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PurchaseOrderService {

    PurchaseOrderRepository purchaseOrderRepository;
    PurchaseOrderItemRepository purchaseOrderItemRepository;
    SupplierRepository supplierRepository;
    WarehouseRepository warehouseRepository;
    UserRepository userRepository;
    ProductRepository productRepository;
    ReceiveOrderItemRepository receiveOrderItemRepository;

    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderCreationRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User createdBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PurchaseOrder order = new PurchaseOrder();
        order.setSupplier(supplier);
        order.setWarehouse(warehouse);
        order.setCreatedBy(createdBy);
        order.setOrderName(request.getOrderName());
        order.setCode(generatePurchaseOrderCode());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");

        List<PurchaseOrderItem> items = new ArrayList<>();
        for (PurchaseOrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(order);
            item.setProduct(product);
            item.setUnitPrice(product.getUnitPrice());
            item.setTaxRate(product.getTaxRate());
            item.setQuantity(itemRequest.getQuantity());
            items.add(item);
        }

        BigDecimal totalPrice = calculateTotalAmount(items);
        order.setTotalPrice(totalPrice);

        PurchaseOrder savedOrder = purchaseOrderRepository.save(order);
        purchaseOrderItemRepository.saveAll(items);

        List<PurchaseOrderItem> savedItems = purchaseOrderItemRepository.findByPurchaseOrderId(savedOrder.getId());

        return mapToPurchaseOrderResponse(savedOrder, savedItems, totalPrice);
    }

    private PurchaseOrderResponse mapToPurchaseOrderResponse(PurchaseOrder order, List<PurchaseOrderItem> items, BigDecimal totalPrice) {
        PurchaseOrderResponse response = new PurchaseOrderResponse();
        response.setId(order.getId());
        response.setCode(order.getCode());
        response.setOrderName(order.getOrderName());
        response.setCreatedAt(order.getCreatedAt());
        response.setStatus(order.getStatus());

        // Map tên Supplier và Warehouse
        response.setSupplierName(order.getSupplier() != null ? order.getSupplier().getName() : null);
        response.setWarehouseName(order.getWarehouse() != null ? order.getWarehouse().getName() : null);

        // Map tên người tạo (createdBy)
        response.setCreatedBy(order.getCreatedBy() != null ? order.getCreatedBy().getFullname() : null);

        // Map items
        List<PurchaseOrderItemResponse> itemResponses = items.stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());
        response.setItems(itemResponses);

        response.setTotalPrice(totalPrice);

        return response;
    }

    private PurchaseOrderItemResponse mapToItemResponse(PurchaseOrderItem item) {
        int received = receiveOrderItemRepository.getTotalReceivedQuantity(item.getId());
        int remaining = item.getQuantity() - received;
        BigDecimal totalPrice = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return PurchaseOrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId().intValue())
                .productCode(item.getProduct().getCode())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .remainingQuantity(remaining)
                .unitPrice(item.getUnitPrice())
                .taxRate(item.getTaxRate())
                .totalPrice(totalPrice)
                .build();
    }

    private String generatePurchaseOrderCode() {
        String prefix = "PO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";

        long countToday = purchaseOrderRepository.countByCreatedAtBetween(
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay()
        );

        return prefix + String.format("%03d", countToday + 1);
    }

    @Transactional
    public PurchaseOrderResponse getPurchaseOrderById(Integer orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.PURCHASE_ORDER_NOT_FOUND));

        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByPurchaseOrderId(order.getId());
        BigDecimal totalPrice = calculateTotalAmount(items);

        return mapToPurchaseOrderResponse(order, items, totalPrice);
    }


    @Transactional
    public PurchaseOrderResponse changePurchaseOrderStatus(Integer orderId, String newStatus) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.PURCHASE_ORDER_NOT_FOUND));

        if (!"PENDING".equals(order.getStatus())) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        switch (newStatus.toUpperCase()) {
            case "APPROVED":
                order.setStatus("APPROVED");
                order.setApprovedBy(currentUser);
                order.setApprovedAt(LocalDateTime.now());
                break;
            case "CANCELLED":
                order.setStatus("CANCELLED");
                order.setApprovedBy(null);
                order.setApprovedAt(null);
                break;
            default:
                throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }

        PurchaseOrder saved = purchaseOrderRepository.save(order);

        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByPurchaseOrderId(saved.getId());
        BigDecimal totalPrice = calculateTotalAmount(items);
        return mapToPurchaseOrderResponse(saved, items, totalPrice);
    }

    public List<PurchaseOrderResponse> getPurchaseOrdersByStatus(String status) {
        List<PurchaseOrder> orders;

        if ("ALL".equalsIgnoreCase(status)) {
            orders = purchaseOrderRepository.findAll();
        } else {
            orders = purchaseOrderRepository.findByStatus(status);
        }

        return orders.stream()
                .map(order -> {
                    List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByPurchaseOrderId(order.getId());
                    BigDecimal totalPrice = calculateTotalAmount(items);
                    return mapToPurchaseOrderResponse(order, items, totalPrice);
                })
                .collect(Collectors.toList());
    }

    public BigDecimal calculateTotalAmount(List<PurchaseOrderItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseOrderItem item : items) {
            BigDecimal unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal taxRate = item.getTaxRate() != null ? item.getTaxRate() : BigDecimal.ZERO;

            BigDecimal itemTotal = unitPrice.multiply(quantity);
            BigDecimal taxAmount = itemTotal.multiply(taxRate.divide(BigDecimal.valueOf(100)));
            total = total.add(itemTotal.add(taxAmount));
        }
        return total;
    }
}
