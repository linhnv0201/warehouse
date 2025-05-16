package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.SupplierCreationRequest;
import com.linh.warehouse.dto.request.SupplierUpdateRequest;
import com.linh.warehouse.dto.response.SupplierResponse;
import com.linh.warehouse.entity.Supplier;
import com.linh.warehouse.exception.AppException;
import com.linh.warehouse.exception.ErrorCode;
import com.linh.warehouse.mapper.SupplierMapper;
import com.linh.warehouse.repository.SupplierRepository;
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
public class SupplierService {

    SupplierRepository supplierRepository;
    SupplierMapper supplierMapper;

    public SupplierResponse createSupplier(SupplierCreationRequest request) {
        if (supplierRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.SUPPLIER_EXISTED);
        }

        Supplier supplier = supplierMapper.toSupplier(request);
        return supplierMapper.toSupplierResponse(supplierRepository.save(supplier));
    }

    public SupplierResponse updateSupplier(Integer id, SupplierUpdateRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));

        if (supplierRepository.existsByName(request.getName()) &&
                !supplier.getName().equalsIgnoreCase(request.getName())) {
            throw new AppException(ErrorCode.SUPPLIER_EXISTED);
        }

        supplierMapper.updateSupplier(supplier, request);
        return supplierMapper.toSupplierResponse(supplierRepository.save(supplier));
    }

    public SupplierResponse getSupplier(Integer id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
        return supplierMapper.toSupplierResponse(supplier);
    }

    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toSupplierResponse)
                .toList();
    }

    public void deleteSupplier(Integer id) {
        if (!supplierRepository.existsById(id)) {
            throw new AppException(ErrorCode.SUPPLIER_NOT_FOUND);
        }
        supplierRepository.deleteById(id);
    }
}
