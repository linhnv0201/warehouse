package com.linh.warehouse.controller;

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
    public ResponseEntity<List<InventoryResponse>> getAllInventories() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryResponse>> getByWarehouseId(@PathVariable Integer warehouseId) {
        return ResponseEntity.ok(inventoryService.getInventoriesByWarehouse(warehouseId));
    }
}

