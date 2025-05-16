package com.linh.warehouse.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierResponse {
    int id;
    String name;
    String companyEmail;
    String phone;
    String address;
}
