package com.linh.warehouse.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    String email;
    String password;
    String fullname;
    String phone;
    String address;
    String role;
}
