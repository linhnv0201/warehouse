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
@Table(name = "sales_orders")
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    String code;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    User createdBy;

    @ManyToOne
    @JoinColumn(name = "approved_by", nullable = true)
    User approvedBy;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @Column(name = "shipping_method", length = 255)
    String shippingMethod;

    @Column(name = "discount_type")
    String discountType;

    @Column(name = "discount_value", precision = 10, scale = 2)
    BigDecimal discountValue;

    @Column(name = "note")
    String note;

    @Column(name = "status", nullable = false, length = 50)
    String status;
}

