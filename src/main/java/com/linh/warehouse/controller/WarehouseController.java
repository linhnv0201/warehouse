package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.ApiResponse;
import com.linh.warehouse.dto.request.WarehouseCreationRequest;
import com.linh.warehouse.dto.request.WarehouseUpdateRequest;
import com.linh.warehouse.dto.response.WarehouseResponse;
import com.linh.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseController {

    WarehouseService warehouseService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    ApiResponse<WarehouseResponse> createWarehouse(@RequestBody @Valid WarehouseCreationRequest request) {
        return ApiResponse.<WarehouseResponse>builder()
                .result(warehouseService.createWarehouse(request))
                .build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    ApiResponse<WarehouseResponse> updateWarehouse(@PathVariable Integer id,
                                                   @RequestBody @Valid WarehouseUpdateRequest request) {
        return ApiResponse.<WarehouseResponse>builder()
                .result(warehouseService.updateWarehouse(id, request))
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER', 'WAREHOUSE', 'ACCOUNTANT')")
    @GetMapping
    ApiResponse<List<WarehouseResponse>> getAllWarehouses() {
        return ApiResponse.<List<WarehouseResponse>>builder()
                .result(warehouseService.getAllWarehouses())
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER', 'WAREHOUSE', 'ACCOUNTANT')")
    @GetMapping("/{id}")
    ApiResponse<WarehouseResponse> getWarehouseById(@PathVariable Integer id) {
        return ApiResponse.<WarehouseResponse>builder()
                .result(warehouseService.getWarehouse(id))
                .build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    String deleteWarehouse(@PathVariable Integer id) {
        warehouseService.deleteWarehouse(id);
        return "Warehouse deleted";
    }
}

