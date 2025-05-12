package com.linh.warehouse.repository;

import com.linh.warehouse.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Integer> {
    // You can add custom query methods if needed
}
