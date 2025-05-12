package com.linh.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "sales_order_items")
public class SalesOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "sales_order_id", nullable = false)
    SalesOrder salesOrder;

    @Column(name = "product_code", length = 255)
    String productCode;

    @Column(name = "name", length = 255)
    String name;

    @Column(name = "quantity")
    int quantity;

    @Column(name = "unit_price", precision = 10, scale = 2)
    BigDecimal unitPrice;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    BigDecimal taxRate;

    @Column(name = "total_price", precision = 12, scale = 2)
    BigDecimal totalPrice;
}

