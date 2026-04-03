package com.innowise.orderservice.controller;

import com.innowise.orderservice.service.dto.order.OrderCreateRequestDto;
import com.innowise.orderservice.service.dto.order.OrderResponseDto;
import com.innowise.orderservice.service.dto.order.OrderUpdateRequestDto;
import com.innowise.orderservice.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.innowise.orderservice.utils.GlobalConstants.DATE;
import static com.innowise.orderservice.utils.GlobalConstants.STATUS;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService<OrderResponseDto, OrderCreateRequestDto,
            OrderUpdateRequestDto, Long> orderService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.readAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN') or @ssi.canAccessOrder(#id)")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.readById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody @Validated OrderCreateRequestDto createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(createRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN') or @ssi.canAccessOrder(#id)")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable Long id,
            @RequestBody @Validated OrderUpdateRequestDto value
    ) {
        return ResponseEntity.ok(orderService.update(id, value));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN') or @ssi.canAccessOrder(#id)")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<OrderResponseDto>> getOrdersWithCriteria(
            @RequestParam String date,
            @RequestParam String status,
            Pageable pageable
    ) {
        Map<String, String> criteria = new HashMap<>();
        criteria.put(DATE, date);
        criteria.put(STATUS, status);
        return ResponseEntity.ok(orderService.readAll(criteria, pageable));
    }
}
