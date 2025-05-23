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
public class SalesOrderItemRequest {
    Integer inventoryId;
    int quantity;
    BigDecimal saleUnitPrice;

}
