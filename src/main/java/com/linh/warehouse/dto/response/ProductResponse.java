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
public class ProductResponse {
    Integer id;
    String code;
    String name;
    String description;
    String unit;
    BigDecimal unitPrice;
    BigDecimal defaultTaxRate;
    Integer supplierId;
    String supplierName;
    LocalDateTime createdAt;
}

