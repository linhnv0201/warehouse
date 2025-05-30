package com.linh.warehouse.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter @Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreationRequest {
    String code;
    String name;
    String description;
    String unit;
    BigDecimal unitPrice;
    BigDecimal taxRate;
    Integer supplierId;
}

