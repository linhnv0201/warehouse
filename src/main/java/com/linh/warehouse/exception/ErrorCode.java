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
    SUPPLIER_EXISTED(1008, "Supplier already existed"),
    SUPPLIER_NOT_FOUND(1009, "Supplier not existed"),
    PURCHASE_ORDER_CODE_EXISTED(1010, "Purchase Order Code already existed"),
    PURCHASE_ORDER_CODE_NOT_FOUND(1011, "Purchase Order Code not found"),
    PURCHASE_ORDER_NOT_FOUND(1012, "Purchase Order not found"),
    INVALID_ORDER_STATUS(1013, "Invalid Order Status"),
    ;
     int code;
     String message;
}
