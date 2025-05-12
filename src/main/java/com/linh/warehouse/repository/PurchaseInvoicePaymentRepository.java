package com.linh.warehouse.repository;

import com.linh.warehouse.entity.PurchaseInvoicePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseInvoicePaymentRepository extends JpaRepository<PurchaseInvoicePayment, Integer> {
    // You can add custom query methods if needed
}
