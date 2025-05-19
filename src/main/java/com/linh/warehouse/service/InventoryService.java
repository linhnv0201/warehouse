package com.linh.warehouse.service;

import com.linh.warehouse.dto.response.InventoryResponse;
import com.linh.warehouse.entity.Inventory;
import com.linh.warehouse.repository.InventoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public List<InventoryResponse> getAllInventories() {
        return inventoryRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<InventoryResponse> getInventoriesByWarehouse(Integer warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(this::mapToDto)
                .toList();
    }

    private InventoryResponse mapToDto(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productCode(inventory.getProductCode())
                .productName(inventory.getProductName())
                .description(inventory.getDescription())
                .quantity(inventory.getQuantity())
                .unit(inventory.getUnit())
                .unitPrice(inventory.getUnitPrice())
                .taxRate(inventory.getTaxRate())
                .lastUpdated(inventory.getLastUpdated())
                .warehouseName(inventory.getWarehouse().getName())
                .build();
    }
}

