package com.linh.warehouse.repository;

import com.linh.warehouse.entity.SaleInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleInvoiceRepository extends JpaRepository<SaleInvoice, Integer> {
    // You can add custom query methods if needed
}
