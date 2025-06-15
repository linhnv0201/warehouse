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
public class SalesOrderResponse {
    Integer id;
    String code;
    String warehouseName;
    String customerName;
    String saleName;
    String status;
    String createdBy;
    LocalDateTime createdAt;
    String approvedBy;
    LocalDateTime approvedAt;
    BigDecimal totalPrice;

    List<SalesOrderItemResponse> items;

}

