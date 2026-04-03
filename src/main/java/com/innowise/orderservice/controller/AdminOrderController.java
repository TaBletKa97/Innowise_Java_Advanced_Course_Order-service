package com.innowise.orderservice.controller;

import com.innowise.orderservice.service.dto.order.OrderResponseDto;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminCreateRequestDto;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminUpdateRequestDto;
import com.innowise.orderservice.service.implementations.OrderServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(("/admin/orders"))
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class AdminOrderController {

    private final OrderServiceImpl orderServiceImpl;

    public AdminOrderController(OrderServiceImpl orderServiceImpl) {
        this.orderServiceImpl = orderServiceImpl;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> adminCreate(
            @RequestBody OrderAdminCreateRequestDto request) {
        OrderResponseDto resp = orderServiceImpl.adminCreate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponseDto> adminUpdate(@PathVariable Long id,
            @RequestBody OrderAdminUpdateRequestDto request) {
        OrderResponseDto resp = orderServiceImpl.adminUpdate(id, request);
        return ResponseEntity.ok(resp);
    }
}
