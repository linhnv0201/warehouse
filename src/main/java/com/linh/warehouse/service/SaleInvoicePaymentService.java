package com.linh.warehouse.service;

import com.linh.warehouse.dto.request.SaleInvoicePaymentRequest;
import com.linh.warehouse.dto.response.SaleInvoicePaymentResponse;
import com.linh.warehouse.entity.SaleInvoice;
import com.linh.warehouse.entity.SaleInvoicePayment;
import com.linh.warehouse.entity.User;
import com.linh.warehouse.exception.AppException;
import com.linh.warehouse.exception.ErrorCode;
import com.linh.warehouse.repository.SaleInvoicePaymentRepository;
import com.linh.warehouse.repository.SaleInvoiceRepository;
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
public class SaleInvoicePaymentService {

    SaleInvoiceRepository saleInvoiceRepository;
    SaleInvoicePaymentRepository paymentRepository;
    UserRepository userRepository;

    public SaleInvoicePayment createPayment(SaleInvoicePaymentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        SaleInvoice invoice = saleInvoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SALE_INVOICE_NOT_FOUND));

        SaleInvoicePayment payment = new SaleInvoicePayment();
        payment.setCode(generatePaymentCode());
        payment.setSaleInvoice(invoice);
        payment.setPaidAt(LocalDateTime.now());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(request.getAmount());
        payment.setNote(request.getNote());
        payment.setCreatedBy(user);

        return paymentRepository.save(payment);
    }

    public List<SaleInvoicePaymentResponse> getPaymentsByInvoiceId(int invoiceId) {
        List<SaleInvoicePayment> list = paymentRepository.findBySaleInvoiceId(invoiceId);
        return list.stream().map(p -> {
            SaleInvoicePaymentResponse res = new SaleInvoicePaymentResponse();
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
        String prefix = "SIP-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        long count = paymentRepository.count();
        return prefix + String.format("%03d", count + 1);
    }
}
