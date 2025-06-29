package com.linh.warehouse.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryResponse {
    int id;
    String productCode;
    String productName;
    String description;
    int quantity;
    int quantityAvailable;
    int quantityReserved;
    int sold;
    String unit;
    BigDecimal unitPrice;
    BigDecimal taxRate;
    LocalDateTime lastUpdated;
    String warehouseName;
}
