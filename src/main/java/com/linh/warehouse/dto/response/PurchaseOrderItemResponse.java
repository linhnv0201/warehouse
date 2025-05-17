package com.linh.warehouse.dto.response;

import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderItemResponse {
    private String productCode;
    private String name;
    private String description;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal taxRate;
    private int quantity;
}

