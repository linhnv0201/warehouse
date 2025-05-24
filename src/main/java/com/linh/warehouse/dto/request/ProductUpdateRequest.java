package com.linh.warehouse.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter @Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequest {
    String code;
    String name;
    String description;
    String unit;
    BigDecimal unitPrice;
    BigDecimal defaultTaxRate;
    Integer supplierId;
}

