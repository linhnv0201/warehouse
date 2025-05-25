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
@Table(name = "purchase_order_items")
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id", nullable = false)
    PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Column(name = "unit_price", precision = 10, scale = 2)
    BigDecimal unitPrice;  // Lưu giá tại thời điểm đặt hàng

    @Column(name = "tax_rate", precision = 5, scale = 2)
    BigDecimal taxRate;    // Lưu thuế tại thời điểm đặt hàng

    @Column(name = "quantity", nullable = false)
    int quantity;
}
