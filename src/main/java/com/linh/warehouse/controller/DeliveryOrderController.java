package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.ApiResponse;
import com.linh.warehouse.dto.request.DeliveryOrderRequest;
import com.linh.warehouse.dto.response.DeliveryOrderResponse;
import com.linh.warehouse.service.DeliveryOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery-orders")
@RequiredArgsConstructor
public class DeliveryOrderController {

    private final DeliveryOrderService deliveryOrderService;

    @PostMapping("/{salesOrderId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAREHOUSE')")
    public ApiResponse<DeliveryOrderResponse> createDeliveryOrder(
            @PathVariable Integer salesOrderId,
            @RequestBody DeliveryOrderRequest request) {
        DeliveryOrderResponse created = deliveryOrderService.createDeliveryOrder(salesOrderId, request);
        return ApiResponse.<DeliveryOrderResponse>builder()
                .message("Tạo phiếu xuất kho thành công")
                .result(created)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<DeliveryOrderResponse> getById(@PathVariable int id) {
        DeliveryOrderResponse deliveryOrder = deliveryOrderService.getById(id);
        return ApiResponse.<DeliveryOrderResponse>builder()
                .message("Lấy phiếu xuất kho thành công")
                .result(deliveryOrder)
                .build();
    }

    @GetMapping("/by-sales-order/{salesOrderId}")
    public ApiResponse<List<DeliveryOrderResponse>> getDObySOId(@PathVariable Integer salesOrderId) {
        List<DeliveryOrderResponse> orders = deliveryOrderService.getBySalesOrderId(salesOrderId);
        return ApiResponse.<List<DeliveryOrderResponse>>builder()
                .message("Lấy danh sách phiếu xuất kho theo đơn bán hàng thành công")
                .result(orders)
                .build();
    }
}

