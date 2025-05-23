package com.linh.warehouse.dto.response;
import java.math.BigDecimal;

import com.linh.warehouse.entity.Inventory;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalesOrderItemResponse {
    Integer id;
    String warehouse;
    int quantity;
    BigDecimal saleUnitPrice;
}
