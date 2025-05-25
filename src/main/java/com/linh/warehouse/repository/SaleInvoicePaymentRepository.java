package com.linh.warehouse.repository;

import com.linh.warehouse.entity.SaleInvoicePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleInvoicePaymentRepository extends JpaRepository<SaleInvoicePayment, Integer> {
    List<SaleInvoicePayment> findBySaleInvoiceId(int invoiceId);
    @Query("SELECT SUM(p.amount) FROM SaleInvoicePayment p WHERE p.saleInvoice.id = :invoiceId")
    Double getTotalPaidAmountByInvoiceId(@Param("invoiceId") int invoiceId);
}
