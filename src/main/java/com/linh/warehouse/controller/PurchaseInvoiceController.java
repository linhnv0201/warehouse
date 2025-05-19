package com.linh.warehouse.controller;

import com.linh.warehouse.service.PurchaseInvoiceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchase-invoices")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PurchaseInvoiceController {

    PurchaseInvoiceService purchaseInvoiceService;

    @GetMapping("/by-receive-order/{receiveOrderId}")
    public ResponseEntity<?> getInvoicesByReceiveOrder(@PathVariable Integer receiveOrderId) {
        return ResponseEntity.ok(purchaseInvoiceService.getInvoicesByReceiveOrderId(receiveOrderId));
    }

}
