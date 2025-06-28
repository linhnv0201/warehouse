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
@Table(name = "delivery_orders")
public class DeliveryOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    String code;

    @ManyToOne
    @JoinColumn(name = "sale_order_id", nullable = false)
    SalesOrder salesOrder;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    User createdBy;

    @Column(name = "created_at")
    LocalDateTime createdAt;

}

