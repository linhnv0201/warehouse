package com.linh.warehouse.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryOrderResponse {

    Integer id;
    String code;
    String warehouseName;
    String customerName;
    String createdBy;
    LocalDateTime createdAt;
    List<DeliveryOrderItemResponse> items;
}
