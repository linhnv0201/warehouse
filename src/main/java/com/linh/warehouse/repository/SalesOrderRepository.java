package com.linh.warehouse.repository;

import com.linh.warehouse.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Integer> {

    List<SalesOrder> findByStatusIgnoreCase(String status);
    List<SalesOrder> findByStatusIn(List<String> statuses);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT COALESCE(SUM(so.total_price), 0) " +
            "FROM sales_orders so " +
            "WHERE so.status = :status", nativeQuery = true)
    BigDecimal sumTotalPriceByStatus(@Param("status") String status);
}
