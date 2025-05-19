package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.ApiResponse;
import com.linh.warehouse.dto.request.CustomerCreationRequest;
import com.linh.warehouse.dto.request.CustomerUpdateRequest;
import com.linh.warehouse.dto.response.CustomerResponse;
import com.linh.warehouse.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerController {

    CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER')")
    public ApiResponse<CustomerResponse> createCustomer(@RequestBody CustomerCreationRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .message("Tạo thành công")
                .result(customerService.createCustomer(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER')")
    public ApiResponse<CustomerResponse> updateCustomer(
            @PathVariable Integer id,
            @RequestBody CustomerUpdateRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .message("Update thành công")
                .result(customerService.updateCustomer(id, request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerResponse> getCustomer(@PathVariable Integer id) {
        return ApiResponse.<CustomerResponse>builder()
                .message("Lấy thành công")
                .result(customerService.getCustomer(id))
                .build();    }

    @GetMapping
    public ApiResponse<List<CustomerResponse>> getAllCustomers() {
        return ApiResponse.<List<CustomerResponse>>builder()
                .message("Toàn bộ khách")
                .result(customerService.getAllCustomers())
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER')")
    public ApiResponse<Void> deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomer(id); // hàm này là void
        return ApiResponse.<Void>builder()
                .message("Xóa khách hàng thành công")
                .result(null)
                .build();
    }

}
