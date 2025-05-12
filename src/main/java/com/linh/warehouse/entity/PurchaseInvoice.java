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
@Table(name = "purchase_invoices")
public class PurchaseInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    String code;

    @ManyToOne
    @JoinColumn(name = "receipt_order_id", nullable = false)
    ReceiveOrder receiveOrder;

    @Column(name = "total_amount", precision = 12, scale = 2)
    BigDecimal totalAmount;

    @Column(name = "note")
    String note;

    @Column(name = "status", nullable = false, length = 50)
    String status;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}

