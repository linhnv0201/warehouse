package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.SaleInvoicePaymentRequest;
import com.linh.warehouse.dto.response.SaleInvoicePaymentResponse;
import com.linh.warehouse.entity.SaleInvoicePayment;
import com.linh.warehouse.service.SaleInvoicePaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sale-invoice-payments")
@RequiredArgsConstructor
@Slf4j
public class SaleInvoicePaymentController {

    private final SaleInvoicePaymentService paymentService;

    @PostMapping
    public ResponseEntity<SaleInvoicePayment> createPayment(@RequestBody SaleInvoicePaymentRequest request) {
        SaleInvoicePayment payment = paymentService.createPayment(request);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<List<SaleInvoicePaymentResponse>> getPaymentsByInvoiceId(@PathVariable int invoiceId) {
        List<SaleInvoicePaymentResponse> payments = paymentService.getPaymentsByInvoiceId(invoiceId);
        return ResponseEntity.ok(payments);
    }
}
