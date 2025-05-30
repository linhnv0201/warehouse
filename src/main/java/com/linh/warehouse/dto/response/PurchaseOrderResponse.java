package com.linh.warehouse.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderResponse {
    Integer id;
    String code;
    String orderName;
    String status;
    LocalDateTime createdAt;
    String createdBy;

    String supplierName;
    String warehouseName;
    BigDecimal totalPrice;

    private List<PurchaseOrderItemResponse> items;
}

