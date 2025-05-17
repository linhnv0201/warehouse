package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.ApiResponse;
import com.linh.warehouse.dto.request.PurchaseOrderCreationRequest;
import com.linh.warehouse.dto.response.PurchaseOrderResponse;
import com.linh.warehouse.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PurchaseOrderController {

    PurchaseOrderService purchaseOrderService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> createPurchaseOrder(@RequestBody PurchaseOrderCreationRequest request) {
        PurchaseOrderResponse response = purchaseOrderService.createPurchaseOrder(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER', 'WAREHOUSE', 'ACCOUNTANT')")
    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponse>> getAllPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{id}/status")
    public PurchaseOrderResponse updateStatus(
            @PathVariable("id") Integer id,
            @RequestParam("status") String status) {
        return purchaseOrderService.changePurchaseOrderStatus(id, status);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER', 'WAREHOUSE', 'ACCOUNTANT')")
    @GetMapping("/approved")
    public List<PurchaseOrderResponse> getApprovedOrders() {
        return purchaseOrderService.getApprovedPurchaseOrders();
    }




}
