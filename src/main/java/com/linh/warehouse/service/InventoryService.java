package com.linh.warehouse.service;

import com.linh.warehouse.dto.response.InventoryResponse;
import com.linh.warehouse.entity.Inventory;
import com.linh.warehouse.mapper.InventoryMapper;
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

    InventoryRepository inventoryRepository;
    InventoryMapper inventoryMapper;


    public List<InventoryResponse> getAllInventories() {
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toInventoryResponse)
                .toList();
    }

    public List<InventoryResponse> getInventoriesByWarehouse(Integer warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(inventoryMapper::toInventoryResponse)
                .toList();
    }

    public List<InventoryResponse> getTop5BestSellers() {
        return inventoryRepository.findTop5ByOrderBySoldDesc().stream()
                .map(inventoryMapper::toInventoryResponse)
                .toList();
    }

    public List<InventoryResponse> getAllBestSellers() {
        return inventoryRepository.findAllByOrderBySoldDesc().stream()
                .map(inventoryMapper::toInventoryResponse)
                .toList();
    }


}

