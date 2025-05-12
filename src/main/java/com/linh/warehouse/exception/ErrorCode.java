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
    INVALID_PASSWORD(1003, "at least 4 characters"),
    ;
     int code;
     String message;
}
