package com.linh.warehouse.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserUpdateRequest {
    String password;
    String fullname;
    String phone;
    String address;
    String role;
}
