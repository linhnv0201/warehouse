package com.linh.warehouse.repository;

import com.linh.warehouse.entity.ReceiveOrderItem;
import jakarta.validation.constraints.DecimalMax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiveOrderItemRepository extends JpaRepository<ReceiveOrderItem, Integer> {
    @Query("SELECT COALESCE(SUM(roi.quantity), 0) FROM ReceiveOrderItem roi WHERE roi.purchaseOrderItem.id = :itemId")
    int getTotalReceivedQuantity(@Param("itemId") int itemId);

    List<ReceiveOrderItem> findByReceiveOrderId(int receiveOrderId);
}
