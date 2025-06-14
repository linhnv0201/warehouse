package com.linh.warehouse.service;

import com.linh.warehouse.dto.response.PurchaseInvoicePaymentResponse;
import com.linh.warehouse.dto.response.PurchaseInvoiceResponse;
import com.linh.warehouse.entity.*;
import com.linh.warehouse.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PurchaseInvoiceService {

    PurchaseInvoiceRepository purchaseInvoiceRepository;
    PurchaseInvoicePaymentRepository purchaseInvoicePaymentRepository;

    public PurchaseInvoiceResponse getInvoiceByReceiveOrderId(Integer receiveOrderId) {
        PurchaseInvoice invoice = purchaseInvoiceRepository.findByReceiveOrderId(receiveOrderId);

        PurchaseInvoiceResponse response = new PurchaseInvoiceResponse();
        response.setId(invoice.getId());
        response.setCode(invoice.getCode());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setStatus(invoice.getStatus());
        response.setCreatedAt(invoice.getCreatedAt());

        ReceiveOrder ro = invoice.getReceiveOrder();
        if (ro != null) {
            response.setReceiveOrderCode(ro.getCode());

            // Lấy supplier từ PO
            PurchaseOrder po = ro.getPurchaseOrder();
            if (po != null && po.getSupplier() != null) {
                response.setSupplierName(po.getSupplier().getName());
            } else {
                response.setSupplierName(null);
            }
        }

        // Tính số tiền còn thiếu
        List<PurchaseInvoicePayment> payments = purchaseInvoicePaymentRepository.findByPurchaseInvoiceId(invoice.getId());
        BigDecimal paidAmount = payments.stream()
                .map(PurchaseInvoicePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingAmount = invoice.getTotalAmount().subtract(paidAmount);
        response.setRemainingAmount(remainingAmount);

        return response;
    }
}
