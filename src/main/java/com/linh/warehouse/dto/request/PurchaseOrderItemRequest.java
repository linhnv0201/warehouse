package com.linh.warehouse.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderItemRequest {

    @NotBlank(message = "productCode không được để trống")
    private String productCode;

    @NotBlank(message = "name không được để trống")
    private String name;

    private String description;

    @NotBlank(message = "unit không được để trống")
    private String unit;

    @NotNull(message = "unitPrice không được để trống")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal taxRate;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private int quantity;
}
