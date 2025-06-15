package com.linh.warehouse.service;

import com.linh.warehouse.entity.PurchaseOrder;
import com.linh.warehouse.entity.SalesOrder;
import com.linh.warehouse.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticService {

    PurchaseOrderRepository purchaseOrderRepository;
    SalesOrderRepository salesOrderRepository;
    InventoryRepository inventoryRepository;
    WarehouseRepository warehouseRepository;

    public BigDecimal getTotalAmountOfApprovedAndCompletedOrders() {
        BigDecimal approvedTotal = purchaseOrderRepository.sumTotalPriceByStatus("APPROVED");
        BigDecimal completedTotal = purchaseOrderRepository.sumTotalPriceByStatus("COMPLETED");
        return approvedTotal.add(completedTotal);
    }

    public List<PurchaseOrder> getApprovedAndCompletedOrders() {
        return purchaseOrderRepository.findByStatusIn(List.of("APPROVED", "COMPLETED"));
    }

    public BigDecimal getTotalAmountOfApprovedAndCompletedSalesOrders() {
        BigDecimal approvedTotal = salesOrderRepository.sumTotalPriceByStatus("APPROVED");
        BigDecimal completedTotal = salesOrderRepository.sumTotalPriceByStatus("COMPLETED");
        return approvedTotal.add(completedTotal);
    }

    public List<SalesOrder> getApprovedAndCompletedSalesOrders() {
        return salesOrderRepository.findByStatusIn(List.of("APPROVED", "COMPLETED"));
    }

}
