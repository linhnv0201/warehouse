package com.linh.warehouse.repository;

import com.linh.warehouse.entity.ReceiveOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiveOrderItemRepository extends JpaRepository<ReceiveOrderItem, Integer> {
    // You can add custom query methods if needed
}
