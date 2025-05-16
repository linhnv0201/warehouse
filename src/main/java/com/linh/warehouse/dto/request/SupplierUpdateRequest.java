package com.linh.warehouse.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter @Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierUpdateRequest {
    String name;
    String companyEmail;
    String phone;
    String address;
}
