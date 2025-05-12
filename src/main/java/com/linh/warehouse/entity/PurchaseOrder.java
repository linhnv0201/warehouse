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
@Table(name = "purchase_orders")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    User createdBy;

    @ManyToOne
    @JoinColumn(name = "approved_by", nullable = true)
    User approvedBy;

    @Column(name = "approved_at")
    LocalDateTime approvedAt;

    @Column(name = "shipping_cost", precision = 10, scale = 2)
    BigDecimal shippingCost;

    @Column(name = "order_name", length = 255)
    String orderName;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    String code;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "status", nullable = false, length = 50)
    String status;
}

