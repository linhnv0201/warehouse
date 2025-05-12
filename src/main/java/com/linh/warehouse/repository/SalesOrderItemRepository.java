package com.linh.warehouse.repository;

import com.linh.warehouse.entity.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Integer> {
    // You can add custom query methods if needed
}
