package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.ApiResponse;
import com.linh.warehouse.dto.request.SalesOrderCreationRequest;
import com.linh.warehouse.dto.response.SalesOrderResponse;
import com.linh.warehouse.service.SalesOrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales-orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SalesOrderController {

    SalesOrderService salesOrderService;

    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER')")
    @PostMapping
    public ApiResponse<SalesOrderResponse> createSalesOrder(
            @RequestBody @Valid SalesOrderCreationRequest request) {
        return ApiResponse.<SalesOrderResponse>builder()
                .message("Thành công")
                .result(salesOrderService.createSalesOrder(request))
                .build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{id}/status")
    public ApiResponse<SalesOrderResponse> updateSalesOrderStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        SalesOrderResponse response = salesOrderService.approveOrDenySalesOrder(id,status);
        return ApiResponse.<SalesOrderResponse>builder()
                .message("Thay đổi trạng thái")
                .result(response)
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER', 'WAREHOUSE', 'ACCOUNTANT')")
    @GetMapping
    public ApiResponse<List<SalesOrderResponse>> getSalesOrdersByStatus(
            @RequestParam(defaultValue = "ALL") String status) {
        List<SalesOrderResponse> responses = salesOrderService.getSalesOrdersByStatus(status);
        return ApiResponse.<List<SalesOrderResponse>>builder()
                .message("Danh sách đơn bán theo trạng thái: " + status)
                .result(responses)
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER', 'WAREHOUSE', 'ACCOUNTANT')")
    @GetMapping("/{id}")
    public ApiResponse<SalesOrderResponse> getSalesOrderById(@PathVariable Integer id) {
        SalesOrderResponse response = salesOrderService.getSalesOrderById(id);
        return ApiResponse.<SalesOrderResponse>builder()
                .message("Lấy chi tiết đơn hàng thành công")
                .result(response)
                .build();
    }

}
