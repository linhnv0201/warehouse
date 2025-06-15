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
    Integer productId;
    String productCode;
    String productName;
    String warehouse;
    Integer quantity;
    Integer remainingQuantity;
    BigDecimal saleUnitPrice;
    BigDecimal taxRate;
    BigDecimal totalPrice;
}
