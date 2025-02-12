package org.example.mollyapi.order.controller;

import lombok.RequiredArgsConstructor;
import org.example.mollyapi.order.dto.OrderRequestDto;
import org.example.mollyapi.order.dto.OrderResponseDto;
import org.example.mollyapi.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestParam Long userId, @RequestBody List<OrderRequestDto> orderRequests) {
        OrderResponseDto response = orderService.createOrder(userId, orderRequests);
        return ResponseEntity.ok(response);
    }
}