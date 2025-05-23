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

    @PreAuthorize("hasAnyRole('MANAGER', 'SALES')")
    @PostMapping
    public ApiResponse<SalesOrderResponse> createSalesOrder(
            @RequestBody @Valid SalesOrderCreationRequest request) {
        return ApiResponse.<SalesOrderResponse>builder()
                .message("Thành công")
                .result(salesOrderService.createSalesOrder(request))
                .build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/status")
    public ApiResponse<SalesOrderResponse> changeSalesOrderStatus(@PathVariable Integer id,
                                                                  @RequestParam String status) {
        return ApiResponse.<SalesOrderResponse>builder()
                .message("Thay đổi trạng thái")
                .result(salesOrderService.approveOrDenySalesOrder(id, status))
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER', 'WAREHOUSE', 'ACCOUNTANT')")
    @GetMapping
    public ApiResponse<List<SalesOrderResponse>> getAllSalesOrders() {
        return ApiResponse.<List<SalesOrderResponse>>builder()
                .message("All SO")
                .result(salesOrderService.getAllSalesOrders())
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER', 'WAREHOUSE', 'ACCOUNTANT')")
    @GetMapping("/approved")
    public ApiResponse<List<SalesOrderResponse>> getApprovedSalesOrders() {
        return ApiResponse.<List<SalesOrderResponse>>builder()
                .message("All Approved SO")
                .result(salesOrderService.getApprovedSalesOrders())
                .build();
    }


}
