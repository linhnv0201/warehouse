package com.linh.warehouse.repository;

import com.linh.warehouse.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {
    List<PurchaseOrder> findByStatus(String status);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT COALESCE(SUM(po.total_price), 0) " +
            "FROM purchase_orders po " +
            "WHERE po.status = :status", nativeQuery = true)
    BigDecimal sumTotalPriceByStatus(@Param("status") String status);

    List<PurchaseOrder> findByStatusIn(List<String> statuses);

}
