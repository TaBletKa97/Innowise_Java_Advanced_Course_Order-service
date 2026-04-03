package com.innowise.orderservice.controller;

import com.innowise.orderservice.service.dto.order.OrderCreateRequestDto;
import com.innowise.orderservice.service.dto.order.OrderResponseDto;
import com.innowise.orderservice.service.dto.order.OrderUpdateRequestDto;
import com.innowise.orderservice.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final OrderService<OrderResponseDto, OrderCreateRequestDto,
                    OrderUpdateRequestDto, Long> orderService;

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.readByUserId(id));
    }
}
