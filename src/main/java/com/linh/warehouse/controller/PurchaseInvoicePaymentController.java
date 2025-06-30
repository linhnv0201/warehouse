package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.ApiResponse;
import com.linh.warehouse.dto.request.PurchaseInvoicePaymentRequest;
import com.linh.warehouse.dto.response.PurchaseInvoicePaymentResponse;
import com.linh.warehouse.service.PurchaseInvoicePaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PurchaseInvoicePaymentController {

    final PurchaseInvoicePaymentService paymentService;

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ACCOUNTANT')")
    public ApiResponse<PurchaseInvoicePaymentResponse> createPayment(
            @PathVariable Integer id,
            @RequestBody PurchaseInvoicePaymentRequest request
    ) {
        PurchaseInvoicePaymentResponse response = paymentService.createPayment(id, request);
        return ApiResponse.<PurchaseInvoicePaymentResponse>builder()
                .message("Tạo thanh toán thành công")
                .result(response)
                .build();
    }

    @GetMapping("/by-invoice/{invoiceId}")
    public ApiResponse<List<PurchaseInvoicePaymentResponse>> getPaymentsByInvoice(@PathVariable int invoiceId) {
        List<PurchaseInvoicePaymentResponse> payments = paymentService.getPaymentsByInvoiceId(invoiceId);
        return ApiResponse.<List<PurchaseInvoicePaymentResponse>>builder()
                .code(0)
                .message("Lấy danh sách thanh toán thành công")
                .result(payments)
                .build();
    }

}


