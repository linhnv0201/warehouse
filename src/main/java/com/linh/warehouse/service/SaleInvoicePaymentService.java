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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Transactional
    public SaleInvoicePaymentResponse createPayment(Integer invoiceId, SaleInvoicePaymentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        SaleInvoice invoice = saleInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.SALE_INVOICE_NOT_FOUND));

        SaleInvoicePayment payment = new SaleInvoicePayment();
        payment.setCode(generatePaymentCode());
        payment.setSaleInvoice(invoice);
        payment.setPaidAt(LocalDateTime.now());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(request.getAmount());
        payment.setNote(request.getNote());
        payment.setCreatedBy(user);

        SaleInvoicePayment savedPayment = paymentRepository.save(payment);

        // Cập nhật trạng thái hóa đơn
        String newStatus = computeInvoicePaymentStatus(invoice.getId());
        if (!newStatus.equals(invoice.getStatus())) {
            invoice.setStatus(newStatus);
            saleInvoiceRepository.save(invoice);
            log.info("Sale invoice {} status updated to {}", invoice.getId(), newStatus);
        }

        return convertToResponse(savedPayment);
    }

    public List<SaleInvoicePaymentResponse> getPaymentsByInvoiceId(int invoiceId) {
        return paymentRepository.findBySaleInvoiceId(invoiceId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public String computeInvoicePaymentStatus(Integer invoiceId) {
        SaleInvoice invoice = saleInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.SALE_INVOICE_NOT_FOUND));

        BigDecimal totalPaid = paymentRepository.getTotalPaidAmountByInvoiceId(invoiceId);
        if (totalPaid == null) {
            totalPaid = BigDecimal.ZERO;
        }

        BigDecimal totalAmount = invoice.getTotalAmount();
        if (totalPaid.compareTo(BigDecimal.ZERO) == 0) {
            return "UNPAID";
        } else if (totalPaid.compareTo(totalAmount) >= 0) {
            return "PAID";
        } else {
            return "PARTIALLY_PAID";
        }
    }

    private String generatePaymentCode() {
        String prefix = "SIP-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        String uniquePart = String.valueOf(System.currentTimeMillis()).substring(7);
        return prefix + uniquePart;
    }

    public SaleInvoicePaymentResponse convertToResponse(SaleInvoicePayment payment) {
        return SaleInvoicePaymentResponse.builder()
                .id(payment.getId())
                .code(payment.getCode())
                .invoiceCode(payment.getSaleInvoice().getCode())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .note(payment.getNote())
                .paidAt(payment.getPaidAt())
                .createdBy(payment.getCreatedBy().getFullname())
                .build();
    }
}
