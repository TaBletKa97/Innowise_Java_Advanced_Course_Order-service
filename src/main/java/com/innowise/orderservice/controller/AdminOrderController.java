package com.innowise.orderservice.controller;

import com.innowise.orderservice.service.dto.order.OrderResponseDto;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminCreateRequestDto;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminUpdateRequestDto;
import com.innowise.orderservice.service.implementations.OrderServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for administrative order management operations.
 * Provides endpoints for creating and updating orders with elevated privileges.
 */
@RestController
@RequestMapping("/admin/orders")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class AdminOrderController {

    private final OrderServiceImpl orderServiceImpl;

    public AdminOrderController(OrderServiceImpl orderServiceImpl) {
        this.orderServiceImpl = orderServiceImpl;
    }

    /**
     * Creates a new order bypassing standard user restrictions.
     *
     * @param request the request DTO containing order details including
     * user ID, status, total price, and list of order items
     *
     * @return the created order as a response DTO wrapped in a
     * HTTP 201 CREATED response
     */
    @PostMapping
    public ResponseEntity<OrderResponseDto> adminCreate(
            @RequestBody OrderAdminCreateRequestDto request) {
        OrderResponseDto resp = orderServiceImpl.adminCreate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    /**
     * Updates an existing order bypassing standard user restrictions.
     *
     * @param id the ID of the order to update
     * @param request the request DTO containing updated order details including
     * user ID, status, total price, and list of order items
     * @return the updated order as a response DTO wrapped in a HTTP 200 OK response
     */
    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponseDto> adminUpdate(@PathVariable Long id,
            @RequestBody OrderAdminUpdateRequestDto request) {
        OrderResponseDto resp = orderServiceImpl.adminUpdate(id, request);
        return ResponseEntity.ok(resp);
    }
}
