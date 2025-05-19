package com.linh.warehouse.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseInvoiceResponse {
    int id;
    String code;
    BigDecimal totalAmount;
    String status;
    LocalDateTime createdAt;
    String receiveOrderCode;
    String supplierName;
}
