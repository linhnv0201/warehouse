package com.linh.warehouse.repository;

import com.linh.warehouse.entity.SalesOrder;
import com.linh.warehouse.entity.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Integer> {
    List<SalesOrderItem> findBySalesOrderId(Integer salesOrderId );
}
