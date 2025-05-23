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
    private Integer id;
    private String code;
    private String orderName;
    private BigDecimal shippingCost;
    private String status;
    private LocalDateTime createdAt;

    private String supplierName;
    private String warehouseName;

    private List<PurchaseOrderItemResponse> items;
}

