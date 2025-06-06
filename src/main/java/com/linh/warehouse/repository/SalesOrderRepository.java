package com.linh.warehouse.repository;

import com.linh.warehouse.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Integer> {
    boolean existsByCode(String code);
    List<SalesOrder> findByStatusIgnoreCase(String status);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}
