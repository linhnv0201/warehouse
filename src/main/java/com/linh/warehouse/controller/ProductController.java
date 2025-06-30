package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.ApiResponse;
import com.linh.warehouse.dto.request.ProductCreationRequest;
import com.linh.warehouse.dto.request.ProductUpdateRequest;
import com.linh.warehouse.dto.response.ProductResponse;
import com.linh.warehouse.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER')")
    public ApiResponse<ProductResponse> createProduct(@RequestBody ProductCreationRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .message("Tạo sản phẩm thành công")
                .result(productService.createProduct(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER')")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductUpdateRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .message("Cập nhật sản phẩm thành công")
                .result(productService.updateProduct(id, request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Integer id) {
        return ApiResponse.<ProductResponse>builder()
                .message("Lấy sản phẩm thành công")
                .result(productService.getProduct(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        return ApiResponse.<List<ProductResponse>>builder()
                .message("Danh sách sản phẩm")
                .result(productService.getAllProducts())
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'PURCHASER')")
    public ApiResponse<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ApiResponse.<Void>builder()
                .message("Xóa sản phẩm thành công")
                .result(null)
                .build();
    }

    @GetMapping("/supplier/{supplierId}")
    public ApiResponse<List<ProductResponse>> getProductsBySupplier(@PathVariable Integer supplierId) {
        List<ProductResponse> products = productService.getProductsBySupplier(supplierId);
        return ApiResponse.<List<ProductResponse>>builder()
                .message("Lấy sản phẩm theo nhà cung cấp thành công")
                .result(products)
                .build();
    }
}
