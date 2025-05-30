//package com.linh.warehouse.service;
//
//import com.linh.warehouse.dto.response.StatisticResponse;
//import com.linh.warehouse.repository.PurchaseInvoiceRepository;
//import com.linh.warehouse.repository.SaleInvoiceRepository;
//import com.linh.warehouse.repository.InventoryRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.AccessLevel;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//@Service
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class StatisticService {
//
//    SaleInvoiceRepository saleInvoiceRepository;
//    PurchaseInvoiceRepository purchaseInvoiceRepository;
//    InventoryRepository inventoryRepository;
//
//    public StatisticResponse getDashboardStatistic(LocalDate fromDate, LocalDate toDate) {
//        BigDecimal totalRevenue = saleInvoiceRepository.getTotalRevenue(fromDate, toDate);
//        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
//
//        BigDecimal totalPurchaseCost = purchaseInvoiceRepository.getTotalPurchaseCost(fromDate, toDate);
//        if (totalPurchaseCost == null) totalPurchaseCost = BigDecimal.ZERO;
//
//        BigDecimal profit = totalRevenue.subtract(totalPurchaseCost);
//
//        Long totalQuantitySold = saleInvoiceRepository.getTotalQuantitySold(fromDate, toDate);
//        if (totalQuantitySold == null) totalQuantitySold = 0L;
//
//        Long totalQuantityPurchased = purchaseInvoiceRepository.getTotalQuantityPurchased(fromDate, toDate);
//        if (totalQuantityPurchased == null) totalQuantityPurchased = 0L;
//
//        Long totalStock = inventoryRepository.getTotalStock();
//        if (totalStock == null) totalStock = 0L;
//
//        return StatisticResponse.builder()
//                .totalRevenue(totalRevenue)
//                .totalPurchaseCost(totalPurchaseCost)
//                .profit(profit)
//                .totalQuantitySold(totalQuantitySold)
//                .totalQuantityPurchased(totalQuantityPurchased)
//                .totalStock(totalStock)
//                .build();
//    }
//}
