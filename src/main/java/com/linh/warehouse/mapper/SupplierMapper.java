package com.linh.warehouse.mapper;

import com.linh.warehouse.dto.request.SupplierCreationRequest;
import com.linh.warehouse.dto.request.SupplierUpdateRequest;
import com.linh.warehouse.dto.response.SupplierResponse;
import com.linh.warehouse.entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    Supplier toSupplier(SupplierCreationRequest request);

    SupplierResponse toSupplierResponse(Supplier supplier);

    void updateSupplier(@MappingTarget Supplier supplier, SupplierUpdateRequest request);
}
