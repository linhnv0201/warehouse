package com.linh.warehouse.repository;

import com.linh.warehouse.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    List<Inventory> findByWarehouseId(Integer warehouseId);
    Optional<Inventory> findByWarehouseIdAndProductCode(Integer warehouseId, String productCode);
    List<Inventory> findByProductCode(String productCode);
    List<Inventory> findTop5ByOrderBySoldDesc();
    List<Inventory> findAllByOrderBySoldDesc();

}
