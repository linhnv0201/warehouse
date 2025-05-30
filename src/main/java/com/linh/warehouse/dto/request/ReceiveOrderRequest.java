package com.linh.warehouse.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiveOrderRequest {
     int purchaseOrderId;
     private BigDecimal shippingCost;
     List<ReceiveOrderItemRequest> items;
}
