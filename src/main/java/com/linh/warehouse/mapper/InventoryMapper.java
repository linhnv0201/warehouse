package com.linh.warehouse.mapper;

import com.linh.warehouse.dto.response.InventoryResponse;
import com.linh.warehouse.entity.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryResponse toInventoryResponse(Inventory inventory);

}
