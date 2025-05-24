package com.linh.warehouse.controller;

import com.linh.warehouse.dto.response.SaleInvoiceResponse;
import com.linh.warehouse.service.SaleInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sale-invoices")
@RequiredArgsConstructor
public class SaleInvoiceController {

    private final SaleInvoiceService saleInvoiceService;

    @GetMapping("/by-delivery-order/{deliveryOrderId}")
    public ResponseEntity<SaleInvoiceResponse> getByDeliveryOrderId(@PathVariable Integer deliveryOrderId) {
        SaleInvoiceResponse response = saleInvoiceService.getInvoiceByDeliveryOrderId(deliveryOrderId);
        return ResponseEntity.ok(response);
    }
}
