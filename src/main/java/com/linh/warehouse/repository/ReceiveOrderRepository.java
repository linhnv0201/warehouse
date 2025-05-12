package com.linh.warehouse.repository;

import com.linh.warehouse.entity.ReceiveOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiveOrderRepository extends JpaRepository<ReceiveOrder, Integer> {
    // You can add custom query methods if needed
}
