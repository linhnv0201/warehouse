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
@Table(name = "purchase_order_items")
public class PurchaseOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id", nullable = false)
    PurchaseOrder purchaseOrder;

    @Column(name = "product_code", length = 255)
    String productCode;

    @Column(name = "name", length = 255)
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "unit", length = 50)
    String unit;

    @Column(name = "unit_price", precision = 10, scale = 2)
    BigDecimal unitPrice;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    BigDecimal taxRate;

    @Column(name = "quantity")
    int quantity;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}
