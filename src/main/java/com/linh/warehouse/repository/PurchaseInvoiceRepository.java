package com.linh.warehouse.repository;

import com.linh.warehouse.entity.PurchaseInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseInvoiceRepository extends JpaRepository<PurchaseInvoice, Integer> {
    PurchaseInvoice findByReceiveOrderId(Integer receiveOrderId);

//    @Query("SELECT SUM(i.totalAmount) FROM PurchaseInvoice i WHERE i.createdAt BETWEEN :from AND :to")
//    BigDecimal getTotalPurchaseCost(@Param("from") LocalDate from, @Param("to") LocalDate to);
//
//    @Query("SELECT SUM(d.quantity) FROM PurchaseInvoice i JOIN i.details d WHERE i.createdAt BETWEEN :from AND :to")
//    Long getTotalQuantityPurchased(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
