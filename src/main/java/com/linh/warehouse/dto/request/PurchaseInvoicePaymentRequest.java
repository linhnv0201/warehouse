package com.linh.warehouse.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseInvoicePaymentRequest {
    private int invoiceId;
    private BigDecimal amount;
    private String paymentMethod;
    private String note;
}
