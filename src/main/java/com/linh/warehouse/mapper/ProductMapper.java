package com.linh.warehouse.mapper;

import com.linh.warehouse.dto.request.ProductCreationRequest;
import com.linh.warehouse.dto.request.ProductUpdateRequest;
import com.linh.warehouse.dto.response.ProductResponse;
import com.linh.warehouse.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "supplier.id", source = "supplierId")
    @Mapping(target = "taxRate", source = "taxRate")  // map từ DTO taxRate -> entity taxRate
    Product toProduct(ProductCreationRequest request);

    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "taxRate", source = "taxRate") // map entity -> response
    ProductResponse toProductResponse(Product product);

    void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);
}

