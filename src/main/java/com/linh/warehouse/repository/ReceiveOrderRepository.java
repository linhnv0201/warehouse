package com.linh.warehouse.repository;

import com.linh.warehouse.entity.ReceiveOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiveOrderRepository extends JpaRepository<ReceiveOrder, Integer> {
    List<ReceiveOrder> findByPurchaseOrderId(Integer purchaseOrderId);

}
