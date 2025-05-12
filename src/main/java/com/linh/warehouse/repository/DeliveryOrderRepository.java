package com.linh.warehouse.repository;

import com.linh.warehouse.entity.DeliveryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Integer> {
    // You can add custom query methods if needed
}
