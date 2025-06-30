package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.ApiResponse;
import com.linh.warehouse.dto.request.ReceiveOrderRequest;
import com.linh.warehouse.dto.response.ReceiveOrderResponse;
import com.linh.warehouse.entity.ReceiveOrder;
import com.linh.warehouse.service.ReceiveOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receive-orders")
@RequiredArgsConstructor
public class ReceiveOrderController {

    private final ReceiveOrderService receiveOrderService;

    @PostMapping("/{purchaseOrderId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAREHOUSE')")
    public ApiResponse<ReceiveOrderResponse> createReceiveOrder(
            @PathVariable Integer purchaseOrderId,
            @RequestBody ReceiveOrderRequest request) {
        ReceiveOrderResponse ro = receiveOrderService.createReceiveOrder(purchaseOrderId, request);
        return ApiResponse.<ReceiveOrderResponse>builder()
                .message("Tạo phiếu nhập kho thành công")
                .result(ro)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ReceiveOrderResponse> getById(@PathVariable int id) {
        ReceiveOrderResponse receiveOrder = receiveOrderService.getById(id);
        return ApiResponse.<ReceiveOrderResponse>builder()
                .message("Lấy phiếu nhập kho thành công")
                .result(receiveOrder)
                .build();
    }

    @GetMapping("/by-purchase-order/{purchaseOrderId}")
    public ApiResponse<List<ReceiveOrder>> getROByPOId(@PathVariable Integer purchaseOrderId) {
        List<ReceiveOrder> orders = receiveOrderService.getByPurchaseOrderId(purchaseOrderId);
        return ApiResponse.<List<ReceiveOrder>>builder()
                .message("Lấy danh sách phiếu nhập kho theo đơn mua thành công")
                .result(orders)
                .build();
    }
}
