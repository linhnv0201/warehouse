package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.PurchaseOrderCreationRequest;
import com.linh.warehouse.dto.request.PurchaseOrderItemRequest;
import com.linh.warehouse.dto.response.PurchaseOrderItemResponse;
import com.linh.warehouse.dto.response.PurchaseOrderResponse;
import com.linh.warehouse.entity.*;
import com.linh.warehouse.exception.AppException;
import com.linh.warehouse.exception.ErrorCode;
import com.linh.warehouse.mapper.PurchaseOrderMapper;
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
    PurchaseOrderMapper purchaseOrderMapper;
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
        order.setShippingCost(request.getShippingCost());
        order.setOrderName(request.getOrderName());
        order.setCode(generatePurchaseOrderCode());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");

        PurchaseOrder savedOrder = purchaseOrderRepository.save(order);

        List<PurchaseOrderItem> items = new ArrayList<>();
        for (PurchaseOrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(savedOrder);
            item.setProduct(product);
            item.setUnitPrice(product.getUnitPrice());
            item.setTaxRate(product.getTaxRate());
            item.setQuantity(itemRequest.getQuantity());
            items.add(item);
        }
        purchaseOrderItemRepository.saveAll(items);

        List<PurchaseOrderItem> savedItems = purchaseOrderItemRepository.findByPurchaseOrderId(savedOrder.getId());
        List<PurchaseOrderItemResponse> itemResponses = savedItems.stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());

        PurchaseOrderResponse response = purchaseOrderMapper.toPurchaseOrderResponse(savedOrder);
        response.setItems(itemResponses);

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
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();
        return getPurchaseOrderResponses(orders);
    }

    private List<PurchaseOrderResponse> getPurchaseOrderResponses(List<PurchaseOrder> orders) {
        return orders.stream().map(this::getPurchaseOrderResponse).collect(Collectors.toList());
    }

    private PurchaseOrderResponse getPurchaseOrderResponse(PurchaseOrder order) {
        PurchaseOrderResponse response = purchaseOrderMapper.toPurchaseOrderResponse(order);
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByPurchaseOrderId(order.getId());

        List<PurchaseOrderItemResponse> itemResponses = items.stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
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
        return getPurchaseOrderResponse(saved);
    }

    public List<PurchaseOrderResponse> getApprovedPurchaseOrders() {
        List<PurchaseOrder> approvedOrders = purchaseOrderRepository.findByStatus("APPROVED");
        return getPurchaseOrderResponses(approvedOrders);
    }
}
