package com.linh.warehouse.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiveOrderResponse {
    Integer id;
    String code;
    String createdBy;
    LocalDateTime createdAt;
    BigDecimal shippingCost;
    BigDecimal totalAmount;
    List<ReceiveOrderItemResponse> items;
}
