package com.linh.warehouse.repository;

import com.linh.warehouse.entity.PurchaseInvoicePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PurchaseInvoicePaymentRepository extends JpaRepository<PurchaseInvoicePayment, Integer> {
    List<PurchaseInvoicePayment> findByPurchaseInvoiceId(Integer invoiceId);
    @Query("SELECT SUM(p.amount) FROM PurchaseInvoicePayment p WHERE p.purchaseInvoice.id = :invoiceId")
    BigDecimal getTotalPaidAmountByInvoiceId(@Param("invoiceId") Integer invoiceId);

}
