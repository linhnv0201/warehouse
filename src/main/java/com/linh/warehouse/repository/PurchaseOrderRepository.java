package com.linh.warehouse.repository;

import com.linh.warehouse.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {
    List<PurchaseOrder> findByStatus(String status);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);


}
