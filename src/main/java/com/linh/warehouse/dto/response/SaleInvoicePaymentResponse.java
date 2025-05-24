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
public class SaleInvoicePaymentResponse {
    private int id;
    private String code;
    private BigDecimal amount;
    private String paymentMethod;
    private String note;
    private LocalDateTime paidAt;
    private String createdByEmail;
}

