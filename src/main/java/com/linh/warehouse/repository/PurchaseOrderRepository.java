package com.linh.warehouse.repository;

import com.linh.warehouse.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {
    boolean existsByCode(String code);
    List<PurchaseOrder> findByStatus(String status);

}
