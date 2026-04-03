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

/**
 * REST controller for managing order entities.
 * Provides endpoints for retrieving, creating, updating, and deleting orders.
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService<OrderResponseDto, OrderCreateRequestDto,
            OrderUpdateRequestDto, Long> orderService;

    /**
     * Retrieves all orders.
     *
     * @return a list of all orders; may be empty but never null
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.readAll());
    }

    /**
     * Retrieves an order by its identifier.
     *
     * @param id the identifier of the order to retrieve; must not be null
     * @return the order response DTO corresponding to the specified identifier
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN') or @ssi.canAccessOrder(#id)")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.readById(id));
    }

    /**
     * Creates a new order.
     *
     * @param createRequest the request DTO containing data required to create
     * the order; must not be null and must be valid according to validation
     * constraints
     * @return the response DTO representing the created order
     */
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody @Validated OrderCreateRequestDto createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(createRequest));
    }

    /**
     * Updates an existing order identified by the given identifier using the
     * provided update request data.
     *
     * @param id the identifier of the order to update; must not be null
     * @param request the request DTO containing the update data; must not be null
     * and must be valid according to validation constraints
     * @return the response DTO representing the updated order
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN') or @ssi.canAccessOrder(#id)")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable Long id,
            @RequestBody @Validated OrderUpdateRequestDto request
    ) {
        return ResponseEntity.ok(orderService.update(id, request));
    }

    /**
     * Deletes the order identified by the given identifier.
     *
     * @param id the identifier of the order to delete; must not be null
     * @return a {@code ResponseEntity} with no content (HTTP 204 No Content)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN') or @ssi.canAccessOrder(#id)")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches for orders based on the provided date and status criteria.
     *
     * @param date the date for filtering orders; must not be null
     * @param status the status for filtering orders; must not be null
     * @param pageable the pagination information; must not be null
     * @return a page of order response DTOs matching the specified criteria
     */
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
