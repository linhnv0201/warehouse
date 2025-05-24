package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.DeliveryOrderRequest;
import com.linh.warehouse.entity.DeliveryOrder;
import com.linh.warehouse.service.DeliveryOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery-orders")
@RequiredArgsConstructor
public class DeliveryOrderController {

    private final DeliveryOrderService deliveryOrderService;

    @PostMapping
    public ResponseEntity<DeliveryOrder> createDeliveryOrder(@RequestBody DeliveryOrderRequest request) {
        DeliveryOrder created = deliveryOrderService.createDeliveryOrder(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryOrder> getById(@PathVariable int id) {
        DeliveryOrder deliveryOrder = deliveryOrderService.getById(id);
        return ResponseEntity.ok(deliveryOrder);
    }

}
