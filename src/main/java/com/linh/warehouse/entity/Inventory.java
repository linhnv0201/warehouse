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
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    Warehouse warehouse;

    @Column(name = "product_code", length = 255, nullable = false)
    String productCode;

    @Column(name = "product_name", length = 255, nullable = false)
    String productName;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "quantity", nullable = false)
    int quantity;

    @Column(name = "quantityAvailable")
    int quantityAvailable;

    @Column(name = "quantityReserved")
    int quantityReserved;

    @Column(name = "unit", length = 50)
    String unit;

    @Column(name = "unit_price", precision = 10, scale = 2)
    BigDecimal unitPrice;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    BigDecimal taxRate;

    @Column(name = "last_updated")
    LocalDateTime lastUpdated;
}
