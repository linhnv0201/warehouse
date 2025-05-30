package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.WarehouseCreationRequest;
import com.linh.warehouse.dto.request.WarehouseUpdateRequest;
import com.linh.warehouse.dto.response.WarehouseResponse;
import com.linh.warehouse.entity.Warehouse;
import com.linh.warehouse.exception.AppException;
import com.linh.warehouse.exception.ErrorCode;
import com.linh.warehouse.mapper.WarehouseMapper;
import com.linh.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WarehouseService {

    WarehouseRepository warehouseRepository;
    WarehouseMapper warehouseMapper;

    public WarehouseResponse createWarehouse(WarehouseCreationRequest request) {
        if (warehouseRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.WAREHOUSE_EXISTED);
        }

        Warehouse warehouse = warehouseMapper.toWarehouse(request);
        return warehouseMapper.toWarehouseResponse(warehouseRepository.save(warehouse));
    }

    public WarehouseResponse updateWarehouse(Integer id, WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND));

        String newName = request.getName();
        if (newName != null && !newName.trim().isEmpty() && !newName.equals(warehouse.getName())) {
            if (warehouseRepository.existsByName(newName)) {
                throw new AppException(ErrorCode.WAREHOUSE_EXISTED);
            }
        }

        warehouseMapper.updateWarehouse(warehouse, request);
        return warehouseMapper.toWarehouseResponse(warehouseRepository.save(warehouse));
    }


    public WarehouseResponse getWarehouse(Integer id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND));
        return warehouseMapper.toWarehouseResponse(warehouse);
    }

    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(warehouseMapper::toWarehouseResponse)
                .toList();
    }

    public void deleteWarehouse(Integer id) {
        if (!warehouseRepository.existsById(id)) {
            throw new AppException(ErrorCode.WAREHOUSE_NOT_FOUND);
        }
        warehouseRepository.deleteById(id);
    }
}
