package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.ApiResponse;
import com.linh.warehouse.dto.request.PurchaseOrderCreationRequest;
import com.linh.warehouse.dto.response.PurchaseOrderResponse;
import com.linh.warehouse.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
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
    public ApiResponse<PurchaseOrderResponse> createPurchaseOrder(@RequestBody PurchaseOrderCreationRequest request) {
        PurchaseOrderResponse response = purchaseOrderService.createPurchaseOrder(request);
        return ApiResponse.<PurchaseOrderResponse>builder()
                .message("Tạo đơn hàng thành công")
                .result(response)
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER', 'WAREHOUSE', 'ACCOUNTANT')")
    @GetMapping
    public ApiResponse<List<PurchaseOrderResponse>> getAllPurchaseOrders() {
        List<PurchaseOrderResponse> responses = purchaseOrderService.getAllPurchaseOrders();
        return ApiResponse.<List<PurchaseOrderResponse>>builder()
                .message("Danh sách đơn hàng")
                .result(responses)
                .build();
    }
    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER', 'WAREHOUSE', 'ACCOUNTANT')")
    @GetMapping("/{id}")
    public ApiResponse<PurchaseOrderResponse> getPurchaseOrderById(@PathVariable Integer id) {
        PurchaseOrderResponse response = purchaseOrderService.getPurchaseOrderById(id);
        return ApiResponse.<PurchaseOrderResponse>builder()
                .message("Chi tiết đơn hàng với id: " + id)
                .result(response)
                .build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{id}/status")
    public ApiResponse<PurchaseOrderResponse> updateStatus(
            @PathVariable("id") Integer id,
            @RequestParam("status") String status) {
        PurchaseOrderResponse response = purchaseOrderService.changePurchaseOrderStatus(id, status);
        return ApiResponse.<PurchaseOrderResponse>builder()
                .message("Cập nhật trạng thái đơn hàng thành công")
                .result(response)
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER', 'WAREHOUSE', 'ACCOUNTANT')")
    @GetMapping("/approved")
    public ApiResponse<List<PurchaseOrderResponse>> getApprovedOrders() {
        List<PurchaseOrderResponse> responses = purchaseOrderService.getApprovedPurchaseOrders();
        return ApiResponse.<List<PurchaseOrderResponse>>builder()
                .message("Danh sách đơn hàng đã duyệt")
                .result(responses)
                .build();
    }
}
