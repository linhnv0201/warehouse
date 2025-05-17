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

import java.time.LocalDateTime;
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


    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderCreationRequest request) {
        // Lấy supplier và warehouse
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User createdBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Tạo đối tượng PO
        PurchaseOrder order = new PurchaseOrder();
        order.setSupplier(supplier);
        order.setWarehouse(warehouse);
        order.setCreatedBy(createdBy);
        order.setShippingCost(request.getShippingCost());
        order.setOrderName(request.getOrderName());
        order.setCode(request.getCode());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");

        PurchaseOrder savedOrder = purchaseOrderRepository.save(order);

        // Chuyển danh sách item request sang entity
        List<PurchaseOrderItem> items = new ArrayList<>();
        for (PurchaseOrderItemRequest itemRequest : request.getItems()) {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(savedOrder);
            item.setProductCode(itemRequest.getProductCode());
            item.setName(itemRequest.getName());
            item.setDescription(itemRequest.getDescription());
            item.setUnit(itemRequest.getUnit());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setTaxRate(itemRequest.getTaxRate());
            item.setQuantity(itemRequest.getQuantity());
            items.add(item);
        }

        purchaseOrderItemRepository.saveAll(items);

        // Build response thủ công
        List<PurchaseOrderItemResponse> itemResponses = items.stream().map(item -> {
            PurchaseOrderItemResponse res = new PurchaseOrderItemResponse();
            res.setProductCode(item.getProductCode());
            res.setName(item.getName());
            res.setDescription(item.getDescription());
            res.setUnit(item.getUnit());
            res.setUnitPrice(item.getUnitPrice());
            res.setTaxRate(item.getTaxRate());
            res.setQuantity(item.getQuantity());
            return res;
        }).collect(Collectors.toList());

        PurchaseOrderResponse response = new PurchaseOrderResponse();
        response.setId(savedOrder.getId());
        response.setCode(savedOrder.getCode());
        response.setOrderName(savedOrder.getOrderName());
        response.setShippingCost(savedOrder.getShippingCost());
        response.setStatus(savedOrder.getStatus());
        response.setCreatedAt(savedOrder.getCreatedAt());
        response.setSupplierName(supplier.getName());
        response.setWarehouseName(warehouse.getName());
        response.setItems(itemResponses);

        return response;
    }


//    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
//        return purchaseOrderRepository.findAll()
//                .stream()
//                .map(purchaseOrderMapper::toPurchaseOrderResponse)
//                .toList();
//    }

    @Transactional
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();

        return orders.stream().map(order -> {
            PurchaseOrderResponse response = new PurchaseOrderResponse();
            response.setId(order.getId());
            response.setCode(order.getCode());
            response.setOrderName(order.getOrderName());
            response.setShippingCost(order.getShippingCost());
            response.setStatus(order.getStatus());
            response.setCreatedAt(order.getCreatedAt());
            response.setSupplierName(order.getSupplier() != null ? order.getSupplier().getName() : null);
            response.setWarehouseName(order.getWarehouse() != null ? order.getWarehouse().getName() : null);

            List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByPurchaseOrderId(order.getId());
            List<PurchaseOrderItemResponse> itemResponses = convertToItemResponses(items);
            response.setItems(itemResponses);

            return response;
        }).toList();
    }

    private List<PurchaseOrderItemResponse> convertToItemResponses(List<PurchaseOrderItem> items) {
        return items.stream().map(item -> {
            PurchaseOrderItemResponse response = new PurchaseOrderItemResponse();
            response.setProductCode(item.getProductCode());
            response.setName(item.getName());
            response.setDescription(item.getDescription());
            response.setUnit(item.getUnit());
            response.setUnitPrice(item.getUnitPrice());
            response.setTaxRate(item.getTaxRate());
            response.setQuantity(item.getQuantity());
            return response;
        }).toList();
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
        return purchaseOrderMapper.toPurchaseOrderResponse(saved);
    }

    public List<PurchaseOrderResponse> getApprovedPurchaseOrders() {
        List<PurchaseOrder> approvedOrders = purchaseOrderRepository.findByStatus("APPROVED");

        return approvedOrders.stream()
                .map(order -> {
                    PurchaseOrderResponse response = purchaseOrderMapper.toPurchaseOrderResponse(order);

                    // Gắn thêm item nếu cần
                    List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByPurchaseOrderId(order.getId());
                    List<PurchaseOrderItemResponse> itemResponses = items.stream()
                            .map(purchaseOrderMapper::toItemResponse)
                            .toList();

                    response.setItems(itemResponses);
                    return response;
                })
                .toList();
    }



}
