//package com.linh.warehouse.mapper;
//
//import com.linh.warehouse.dto.request.PurchaseOrderCreationRequest;
//import com.linh.warehouse.dto.request.PurchaseOrderItemRequest;
//import com.linh.warehouse.dto.response.PurchaseOrderItemResponse;
//import com.linh.warehouse.dto.response.PurchaseOrderResponse;
//import com.linh.warehouse.entity.PurchaseOrder;
//import com.linh.warehouse.entity.PurchaseOrderItem;
//import org.mapstruct.*;
//
//import java.util.List;
//
//@Mapper(componentModel = "spring")
//public interface PurchaseOrderMapper {
//
//    @Mapping(source = "createdBy", target = "createdBy")
//    @Mapping(source = "supplier.name", target = "supplierName")
//    @Mapping(source = "warehouse.name", target = "warehouseName")
//    PurchaseOrderResponse toPurchaseOrderResponse(PurchaseOrder purchaseOrder);
//
//    PurchaseOrder toPurchaseOrder(PurchaseOrderCreationRequest request);
//
//    PurchaseOrderItem toItem(PurchaseOrderItemRequest request);
//
//    List<PurchaseOrderItem> toItemList(List<PurchaseOrderItemRequest> requestList);
//
//    PurchaseOrderItemResponse toItemResponse(PurchaseOrderItem item);
//
//    List<PurchaseOrderItemResponse> toItemResponseList(List<PurchaseOrderItem> itemList);
//
//    // Mapping User -> String (fullname)
//    default String map(com.linh.warehouse.entity.User user) {
//        if (user == null) return null;
//        return user.getFullname();
//    }
//}
//
//
