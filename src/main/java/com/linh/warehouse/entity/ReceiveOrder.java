package com.linh.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "receive_orders")
public class ReceiveOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    String code;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id", nullable = false)
    PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    User createdBy;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}

