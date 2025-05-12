package com.linh.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "delivery_order_items")
public class DeliveryOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "delivery_order_id", nullable = false)
    DeliveryOrder deliveryOrder;

    @ManyToOne
    @JoinColumn(name = "sales_order_item_id", nullable = false)
    SalesOrderItem salesOrderItem;

    @Column(name = "quantity")
    int quantity;
}
