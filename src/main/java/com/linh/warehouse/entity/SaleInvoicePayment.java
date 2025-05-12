package com.linh.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "sale_invoice_payments")
public class SaleInvoicePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    String code;

    @ManyToOne
    @JoinColumn(name = "sale_invoice_id", nullable = false)
    SaleInvoice saleInvoice;

    @Column(name = "paid_at", nullable = false)
    LocalDateTime paidAt;

    @Column(name = "payment_method", nullable = false, length = 50)
    String paymentMethod;  // Simplified to a String

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    BigDecimal amount;

    @Column(name = "note")
    String note;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    User createdBy;
}

