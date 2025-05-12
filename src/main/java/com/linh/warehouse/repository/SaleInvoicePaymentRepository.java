package com.linh.warehouse.repository;

import com.linh.warehouse.entity.SaleInvoicePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleInvoicePaymentRepository extends JpaRepository<SaleInvoicePayment, Integer> {
    // You can add custom query methods if needed
}
