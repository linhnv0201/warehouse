package com.linh.warehouse.repository;

import com.linh.warehouse.entity.PurchaseInvoicePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseInvoicePaymentRepository extends JpaRepository<PurchaseInvoicePayment, Integer> {
    List<PurchaseInvoicePayment> findByPurchaseInvoiceId(Integer invoiceId);
}
