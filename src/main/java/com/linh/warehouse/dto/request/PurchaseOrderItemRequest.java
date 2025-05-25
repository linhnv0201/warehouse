package com.linh.warehouse.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderItemRequest {

    @NotNull(message = "productId không được để trống")
    Integer productId;

    @NotNull(message = "quantity không được để trống")
    @Positive(message = "quantity phải lớn hơn 0")
    Integer quantity;
}
