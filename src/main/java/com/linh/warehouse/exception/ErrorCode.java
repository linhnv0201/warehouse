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
    PURCHASE_ORDER_ITEM_NOT_FOUND(1013, "Purchase Order item not found"),
    INVALID_ORDER_STATUS(1014, "Invalid Order Status"),
    QUANTITY_EXCEEDS_PURCHASE_ORDER(1015, "Quantity Exceeds Purchase Order"),
    PURCHASE_INVOICE_NOT_FOUND(1016, "Purchase Invoice not found"),
    CUSTOMER_EXISTED(1017, "Customer already existed"),
    CUSTOMER_NOT_FOUND(1018, "Customer not found"),
    SALES_ORDER_CODE_EXISTED(1019, "Sales Order Code already existed"),
    SALES_ORDER_NOT_FOUND(1020, "Sales Order not found"),
    INVENTORY_NOT_FOUND(1021, "Inventory not found"),
    INSUFFICIENT_INVENTORY(1022, "Insufficient Inventory"),
    SALES_ORDER_ITEM_NOT_FOUND(1023, "Sales Order Item Not Found"),
    QUANTITY_EXCEEDS_SALES_ORDER(1024, "Quantity Exceeds Sales Order"),
    DELIVERY_ORDER_NOT_FOUND(1025, "Delivery Order not found"),
    RECEIVE_ORDER_NOT_FOUND(1026, "Receive Order not found"),
    SALE_INVOICE_NOT_FOUND(1027, "Sale Invoice not found"),
    PRODUCT_EXISTED(1028, "Product already existed"),
    PRODUCT_NOT_FOUND(1029, "Product not found"),
    ;
     int code;
     String message;

}
