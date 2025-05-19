package com.linh.warehouse.repository;

import com.linh.warehouse.entity.PurchaseInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseInvoiceRepository extends JpaRepository<PurchaseInvoice, Integer> {
    List<PurchaseInvoice> findByReceiveOrderId(Integer receiveOrderId);
}
