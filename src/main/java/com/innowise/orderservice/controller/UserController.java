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

/**
 * REST controller for managing user-related orders.
 * Provides endpoints to retrieve orders associated with a specific user.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final OrderService<OrderResponseDto, OrderCreateRequestDto,
                    OrderUpdateRequestDto, Long> orderService;

    /**
     * Retrieves a list of orders associated with the specified user ID.
     *
     * @param id the ID of the user whose orders are to be retrieved;
     * must be non-null
     * @return a list of order response DTOs belonging to the user;
     * may be empty but never null
     */
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.readByUserId(id));
    }
}
