package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.ApiResponse;
import com.linh.warehouse.dto.response.InventoryResponse;
import com.linh.warehouse.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ApiResponse<List<InventoryResponse>> getAllInventories() {
    return ApiResponse.<List<InventoryResponse>>builder()
            .message("Lấy all inventories")
            .result(inventoryService.getAllInventories())
            .build();
        //        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ApiResponse<List<InventoryResponse>> getByWarehouseId(@PathVariable Integer warehouseId) {
        return ApiResponse.<List<InventoryResponse>>builder()
                .message("Lấy all inventories của 1 warehouse")
                .result(inventoryService.getInventoriesByWarehouse(warehouseId))
                .build();
    }

    @GetMapping("/best-sellers/top5")
    public ApiResponse<List<InventoryResponse>> getTop5BestSellers() {
        return ApiResponse.<List<InventoryResponse>>builder()
                .message("Top 5 sản phẩm bán chạy nhất")
                .result(inventoryService.getTop5BestSellers())
                .build();
    }

    @GetMapping("/best-sellers")
    public ApiResponse<List<InventoryResponse>> getAllBestSellers() {
        return ApiResponse.<List<InventoryResponse>>builder()
                .message("Danh sách tất cả sản phẩm bán chạy nhất")
                .result(inventoryService.getAllBestSellers())
                .build();
    }


}

