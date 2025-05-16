package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.ApiResponse;
import com.linh.warehouse.dto.request.SupplierCreationRequest;
import com.linh.warehouse.dto.request.SupplierUpdateRequest;
import com.linh.warehouse.dto.response.SupplierResponse;
import com.linh.warehouse.service.SupplierService;
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
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupplierController {

    SupplierService supplierService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    ApiResponse<SupplierResponse> createSupplier(@RequestBody @Valid SupplierCreationRequest request) {
        return ApiResponse.<SupplierResponse>builder()
                .result(supplierService.createSupplier(request))
                .build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    ApiResponse<SupplierResponse> updateSupplier(@PathVariable Integer id,
                                                 @RequestBody @Valid SupplierUpdateRequest request) {
        return ApiResponse.<SupplierResponse>builder()
                .result(supplierService.updateSupplier(id, request))
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER', 'WAREHOUSE', 'ACCOUNTANT')")
    @GetMapping
    ApiResponse<List<SupplierResponse>> getAllSuppliers() {
        return ApiResponse.<List<SupplierResponse>>builder()
                .result(supplierService.getAllSuppliers())
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER', 'WAREHOUSE', 'ACCOUNTANT')")
    @GetMapping("/{id}")
    ApiResponse<SupplierResponse> getSupplierById(@PathVariable Integer id) {
        return ApiResponse.<SupplierResponse>builder()
                .result(supplierService.getSupplier(id))
                .build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    String deleteSupplier(@PathVariable Integer id) {
        supplierService.deleteSupplier(id);
        return "Supplier deleted";
    }
}
