package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.ProductCreationRequest;
import com.linh.warehouse.dto.request.ProductUpdateRequest;
import com.linh.warehouse.dto.response.ProductResponse;
import com.linh.warehouse.entity.Product;
import com.linh.warehouse.entity.Supplier;
import com.linh.warehouse.exception.AppException;
import com.linh.warehouse.exception.ErrorCode;
import com.linh.warehouse.mapper.ProductMapper;
import com.linh.warehouse.repository.ProductRepository;
import com.linh.warehouse.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {

    ProductRepository productRepository;
    SupplierRepository supplierRepository;
    ProductMapper productMapper;

    public ProductResponse createProduct(ProductCreationRequest request) {
        if (productRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));

        Product product = productMapper.toProduct(request);
        product.setSupplier(supplier);
        product.setCreatedAt(LocalDateTime.now());

        return productMapper.toProductResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(Integer id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (productRepository.existsByCode(request.getCode()) &&
                !product.getCode().equalsIgnoreCase(request.getCode())) {
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        productMapper.updateProduct(product, request);

        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
            product.setSupplier(supplier);
        }

        return productMapper.toProductResponse(productRepository.save(product));
    }

    public ProductResponse getProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toProductResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        productRepository.deleteById(id);
    }

    public List<ProductResponse> getProductsBySupplier(Integer supplierId) {
        List<Product> products = productRepository.findBySupplierId(supplierId);

        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Không tìm thấy sản phẩm nào cho nhà cung cấp id = " + supplierId);
        }

        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }
}
