package com.linh.warehouse.repository;

import com.linh.warehouse.entity.SaleInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaleInvoiceRepository extends JpaRepository<SaleInvoice, Integer> {
    Optional<SaleInvoice> findByDeliveryOrderId(Integer deliveryOrderId);

}
