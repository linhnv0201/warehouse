package com.linh.warehouse.mapper;

import com.linh.warehouse.dto.response.InventoryResponse;
import com.linh.warehouse.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(source = "warehouse.name", target = "warehouseName")
    InventoryResponse toInventoryResponse(Inventory inventory);

}
