package com.linh.warehouse.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderItemResponse {
    Integer id;
    Integer productId;
    String productCode;
    String productName;
    Integer quantity;
    Integer remainingQuantity;
    BigDecimal unitPrice;
    BigDecimal totalPrice;
}

