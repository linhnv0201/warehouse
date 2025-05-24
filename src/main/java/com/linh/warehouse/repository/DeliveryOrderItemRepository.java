package com.linh.warehouse.repository;

import com.linh.warehouse.entity.DeliveryOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryOrderItemRepository extends JpaRepository<DeliveryOrderItem, Integer> {
    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM DeliveryOrderItem d WHERE d.salesOrderItem.id = :salesOrderItemId")
    int getTotalDeliveredQuantity(@Param("salesOrderItemId") int salesOrderItemId);}
