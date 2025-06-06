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
public class PurchaseInvoicePaymentResponse {
    int id;
    String code;
    String invoiceCode;
    BigDecimal amount;
    String paymentMethod;
    String note;
    LocalDateTime paidAt;
    String createdByName;
}

