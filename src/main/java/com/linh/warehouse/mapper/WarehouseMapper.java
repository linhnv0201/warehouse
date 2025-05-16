package com.linh.warehouse.mapper;

import com.linh.warehouse.dto.request.WarehouseCreationRequest;
import com.linh.warehouse.dto.request.WarehouseUpdateRequest;
import com.linh.warehouse.dto.response.WarehouseResponse;
import com.linh.warehouse.entity.Warehouse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    Warehouse toWarehouse(WarehouseCreationRequest request);
    WarehouseResponse toWarehouseResponse(Warehouse warehouse);
    void updateWarehouse(@MappingTarget Warehouse warehouse, WarehouseUpdateRequest request);
}
