package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.ApiResponse;
import com.linh.warehouse.dto.request.SaleInvoicePaymentRequest;
import com.linh.warehouse.dto.response.SaleInvoicePaymentResponse;
import com.linh.warehouse.entity.SaleInvoicePayment;
import com.linh.warehouse.service.SaleInvoicePaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sale-invoice-payments")
@RequiredArgsConstructor
@Slf4j
public class SaleInvoicePaymentController {

    private final SaleInvoicePaymentService saleInvoicePaymentService;

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ACCOUNTANT')")
    public ApiResponse<SaleInvoicePaymentResponse> createPayment(
            @PathVariable Integer id,
            @RequestBody SaleInvoicePaymentRequest request
    ) {
        SaleInvoicePaymentResponse response = saleInvoicePaymentService.createPayment(id, request);
        return ApiResponse.<SaleInvoicePaymentResponse>builder()
                .message("Tạo thanh toán thành công")
                .result(response)
                .build();
    }


    @GetMapping("/{invoiceId}")
    public ResponseEntity<List<SaleInvoicePaymentResponse>> getPaymentsByInvoiceId(@PathVariable int invoiceId) {
        List<SaleInvoicePaymentResponse> payments = saleInvoicePaymentService.getPaymentsByInvoiceId(invoiceId);
        return ResponseEntity.ok(payments);
    }
}
