package com.linh.warehouse.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticResponse {
    BigDecimal totalRevenue;
    BigDecimal totalPurchaseCost;
    BigDecimal profit;
    Long totalQuantitySold;
    Long totalQuantityPurchased;
    Long totalStock;
}
