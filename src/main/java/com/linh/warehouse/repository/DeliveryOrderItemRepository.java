package com.linh.warehouse.repository;

import com.linh.warehouse.entity.DeliveryOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryOrderItemRepository extends JpaRepository<DeliveryOrderItem, Integer> {
    @Query("SELECT COALESCE(SUM(doi.quantity), 0) FROM DeliveryOrderItem doi WHERE doi.salesOrderItem.id = :itemId")
    int getTotalDeliveredQuantity(@Param("itemId") int itemId);

    List<DeliveryOrderItem> findByDeliveryOrderId(int deliveryOrderId);
}
