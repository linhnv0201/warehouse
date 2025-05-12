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
@Table(name = "receive_order_items")
public class ReceiveOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "receipt_order_id", nullable = false)
    ReceiveOrder receiveOrder;

    @ManyToOne
    @JoinColumn(name = "purchase_order_item_id", nullable = false)
    PurchaseOrderItem purchaseOrderItem;

    @Column(name = "quantity")
    int quantity;
}
