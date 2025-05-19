package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.PurchaseInvoicePaymentRequest;
import com.linh.warehouse.service.PurchaseInvoicePaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PurchaseInvoicePaymentController {

    final PurchaseInvoicePaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PurchaseInvoicePaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @GetMapping("/by-invoice/{invoiceId}")
    public ResponseEntity<?> getPaymentsByInvoice(@PathVariable int invoiceId) {
        return ResponseEntity.ok(paymentService.getPaymentsByInvoiceId(invoiceId));
    }
}


