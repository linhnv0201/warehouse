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
public class ReceiveOrderItemResponse {
    Integer id;
    String productName;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal taxRate;
    BigDecimal totalPrice;
}
