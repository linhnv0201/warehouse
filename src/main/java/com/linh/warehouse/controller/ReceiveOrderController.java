package com.linh.warehouse.controller;

import com.linh.warehouse.dto.request.ReceiveOrderRequest;
import com.linh.warehouse.entity.ReceiveOrder;
import com.linh.warehouse.service.ReceiveOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receive-orders")
@RequiredArgsConstructor
public class ReceiveOrderController {

    private final ReceiveOrderService receiveOrderService;

    @PostMapping
    public ResponseEntity<?> createReceiveOrder(@RequestBody ReceiveOrderRequest request) {
        ReceiveOrder ro = receiveOrderService.createReceiveOrder(request);
        return ResponseEntity.ok(ro);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceiveOrder> getById(@PathVariable int id) {
        ReceiveOrder receiveOrder = receiveOrderService.getById(id);
        return ResponseEntity.ok(receiveOrder);
    }


}
