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
public class DeliveryOrderItemResponse {

    Integer id;
    Integer productId;
    String productCode;
    String productName;
    String warehouse;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal totalPrice;
}
