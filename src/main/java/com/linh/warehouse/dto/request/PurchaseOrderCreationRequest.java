package com.linh.warehouse.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class PurchaseOrderCreationRequest {

    @NotNull(message = "supplierId không được để trống")
    private Integer supplierId;

    @NotNull(message = "warehouseId không được để trống")
    private Integer warehouseId;

    @NotBlank(message = "orderName không được để trống")
    private String orderName;

    @NotEmpty(message = "Danh sách món hàng không được để trống")
    private List<PurchaseOrderItemRequest> items;
}

