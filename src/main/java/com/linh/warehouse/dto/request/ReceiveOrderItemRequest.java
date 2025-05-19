package com.linh.warehouse.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiveOrderItemRequest {
    private int purchaseOrderItemId;
    private int quantity;
}

