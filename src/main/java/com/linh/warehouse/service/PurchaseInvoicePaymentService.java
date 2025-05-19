package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.PurchaseInvoicePaymentRequest;
import com.linh.warehouse.dto.response.PurchaseInvoicePaymentResponse;
import com.linh.warehouse.entity.PurchaseInvoice;
import com.linh.warehouse.entity.PurchaseInvoicePayment;
import com.linh.warehouse.entity.User;
import com.linh.warehouse.exception.AppException;
import com.linh.warehouse.exception.ErrorCode;
import com.linh.warehouse.repository.PurchaseInvoicePaymentRepository;
import com.linh.warehouse.repository.PurchaseInvoiceRepository;
import com.linh.warehouse.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PurchaseInvoicePaymentService {

    PurchaseInvoiceRepository purchaseInvoiceRepository;
    PurchaseInvoicePaymentRepository paymentRepository;
    UserRepository userRepository;

    public PurchaseInvoicePayment createPayment(PurchaseInvoicePaymentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PurchaseInvoice invoice = purchaseInvoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new AppException(ErrorCode.PURCHASE_INVOICE_NOT_FOUND));

        PurchaseInvoicePayment payment = new PurchaseInvoicePayment();
        payment.setCode(generatePaymentCode());
        payment.setPurchaseInvoice(invoice);
        payment.setPaidAt(LocalDateTime.now());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(request.getAmount());
        payment.setNote(request.getNote());
        payment.setCreatedBy(user);

        return paymentRepository.save(payment);
    }

    public List<PurchaseInvoicePaymentResponse> getPaymentsByInvoiceId(int invoiceId) {
        List<PurchaseInvoicePayment> list = paymentRepository.findByPurchaseInvoiceId(invoiceId);
        return list.stream().map(p -> {
            PurchaseInvoicePaymentResponse res = new PurchaseInvoicePaymentResponse();
            res.setId(p.getId());
            res.setCode(p.getCode());
            res.setAmount(p.getAmount());
            res.setPaymentMethod(p.getPaymentMethod());
            res.setNote(p.getNote());
            res.setPaidAt(p.getPaidAt());
            res.setCreatedByEmail(p.getCreatedBy().getEmail());
            return res;
        }).collect(Collectors.toList());
    }

    private String generatePaymentCode() {
        String prefix = "PIP-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        long count = paymentRepository.count();
        return prefix + String.format("%03d", count + 1);
    }
}

