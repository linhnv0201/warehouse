package com.linh.warehouse.repository;

import com.linh.warehouse.entity.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Integer> {
    List<DeliveryOrder> findBySalesOrderId(Integer deliveryId);
}
