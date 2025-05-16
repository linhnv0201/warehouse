package com.linh.warehouse.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(999, "Uncategorized Exception"),
    INVALID_KEY(1001, "Invalid Message Key"),
    USER_EXISTED(1002, "User already existed"),
    USER_NOT_EXISTED(1005, "User not existed"),
    INVALID_PASSWORD(1003, "at least 4 characters"),
    UNAUTHENTICATED(1004, "Unauthenticated"),
    WAREHOUSE_EXISTED(1006, "Warehouse name already existed"),
    WAREHOUSE_NOT_FOUND(1007, "Warehouse not found"),
    ;
     int code;
     String message;
}
