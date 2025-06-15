package com.linh.warehouse.controller;

import com.linh.warehouse.dto.response.StatisticResponse;
import com.linh.warehouse.entity.PurchaseOrder;
import com.linh.warehouse.entity.SalesOrder;
import com.linh.warehouse.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StatisticController {

    StatisticService statisticService;


    @GetMapping("/purchase-orders/total-approved-completed")
    public BigDecimal getTotalAmountOfApprovedAndCompletedOrders() {
        return statisticService.getTotalAmountOfApprovedAndCompletedOrders();
    }

    @GetMapping("/purchase-orders/approved-completed")
    public List<PurchaseOrder> getApprovedAndCompletedOrders() {
        return statisticService.getApprovedAndCompletedOrders();
    }

    @GetMapping("/sales-orders/total-approved-completed")
    public BigDecimal getTotalAmountOfApprovedAndCompletedSalesOrders() {
        return statisticService.getTotalAmountOfApprovedAndCompletedSalesOrders();
    }

    @GetMapping("/sales-orders/approved-completed")
    public List<SalesOrder> getApprovedAndCompletedSalesOrders() {
        return statisticService.getApprovedAndCompletedSalesOrders();
    }
}
