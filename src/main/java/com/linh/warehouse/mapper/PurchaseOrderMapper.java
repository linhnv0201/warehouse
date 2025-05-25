package com.linh.warehouse.mapper;

import com.linh.warehouse.dto.request.PurchaseOrderCreationRequest;
import com.linh.warehouse.dto.request.PurchaseOrderItemRequest;
import com.linh.warehouse.dto.response.PurchaseOrderItemResponse;
import com.linh.warehouse.dto.response.PurchaseOrderResponse;
import com.linh.warehouse.entity.PurchaseOrder;
import com.linh.warehouse.entity.PurchaseOrderItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {

    // Request -> Entity
    PurchaseOrder toPurchaseOrder(PurchaseOrderCreationRequest request);

    PurchaseOrderItem toItem(PurchaseOrderItemRequest request);

    List<PurchaseOrderItem> toItemList(List<PurchaseOrderItemRequest> requestList);

    // Entity -> Response
    PurchaseOrderResponse toPurchaseOrderResponse(PurchaseOrder purchaseOrder);

    PurchaseOrderItemResponse toItemResponse(PurchaseOrderItem item);

    List<PurchaseOrderItemResponse> toItemResponseList(List<PurchaseOrderItem> itemList);
}
