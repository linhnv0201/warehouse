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
public class PurchaseInvoicePaymentService {

    PurchaseInvoiceRepository purchaseInvoiceRepository;
    PurchaseInvoicePaymentRepository paymentRepository;
    UserRepository userRepository;

    public PurchaseInvoicePaymentResponse createPayment(Integer invoiceId, PurchaseInvoicePaymentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PurchaseInvoice invoice = purchaseInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.PURCHASE_INVOICE_NOT_FOUND));

        // Lấy tổng số tiền đã thanh toán
        List<PurchaseInvoicePayment> payments = paymentRepository.findByPurchaseInvoiceId(invoiceId);
        BigDecimal totalPaid = payments.stream()
                .map(PurchaseInvoicePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingAmount = invoice.getTotalAmount().subtract(totalPaid);

        // Kiểm tra nếu số tiền thanh toán lớn hơn số còn lại
        if (request.getAmount().compareTo(remainingAmount) > 0) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        PurchaseInvoicePayment payment = new PurchaseInvoicePayment();
        payment.setCode(generatePaymentCode());
        payment.setPurchaseInvoice(invoice);
        payment.setPaidAt(LocalDateTime.now());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(request.getAmount());
        payment.setNote(request.getNote());
        payment.setCreatedBy(user);

        PurchaseInvoicePayment savedPayment = paymentRepository.save(payment);

        // Cập nhật trạng thái hóa đơn
        String newStatus = computeInvoicePaymentStatus(invoiceId);
        if (!newStatus.equals(invoice.getStatus())) {
            invoice.setStatus(newStatus);
            purchaseInvoiceRepository.save(invoice);
            log.info("Đã cập nhật status cho hóa đơn {} thành {}", invoice.getId(), newStatus);
        }

        return convertToResponse(savedPayment);
    }


    public List<PurchaseInvoicePaymentResponse> getPaymentsByInvoiceId(int invoiceId) {
        List<PurchaseInvoicePayment> list = paymentRepository.findByPurchaseInvoiceId(invoiceId);
        return list.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public String computeInvoicePaymentStatus(Integer invoiceId) {
        PurchaseInvoice invoice = purchaseInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.PURCHASE_INVOICE_NOT_FOUND));

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

    public PurchaseInvoicePaymentResponse convertToResponse(PurchaseInvoicePayment payment) {
        return PurchaseInvoicePaymentResponse.builder()
                .id(payment.getId())
                .code(payment.getCode())
                .invoiceCode(payment.getPurchaseInvoice().getCode())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .note(payment.getNote())
                .paidAt(payment.getPaidAt())
                .createdByName(payment.getCreatedBy().getFullname())
                .build();
    }

    private String generatePaymentCode() {
        String prefix = "PIP-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        long count = paymentRepository.count();
        return prefix + String.format("%03d", count + 1);
    }


}

