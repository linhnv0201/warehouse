package com.linh.warehouse.repository;

import com.linh.warehouse.entity.SaleInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SaleInvoiceRepository extends JpaRepository<SaleInvoice, Integer> {
    Optional<SaleInvoice> findByDeliveryOrderId(Integer deliveryOrderId);

//    @Query("SELECT SUM(i.totalAmount) FROM SaleInvoice i WHERE i.createdAt BETWEEN :from AND :to")
//    BigDecimal getTotalRevenue(@Param("from") LocalDate from, @Param("to") LocalDate to);
//
//    @Query("SELECT SUM(d.quantity) FROM SaleInvoice i JOIN i.details d WHERE i.createdAt BETWEEN :from AND :to")
//    Long getTotalQuantitySold(@Param("from") LocalDate from, @Param("to") LocalDate to);

}
