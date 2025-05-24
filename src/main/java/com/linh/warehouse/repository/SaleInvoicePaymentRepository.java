package com.linh.warehouse.repository;

import com.linh.warehouse.entity.SaleInvoicePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleInvoicePaymentRepository extends JpaRepository<SaleInvoicePayment, Integer> {
    List<SaleInvoicePayment> findBySaleInvoiceId(int invoiceId);
}
