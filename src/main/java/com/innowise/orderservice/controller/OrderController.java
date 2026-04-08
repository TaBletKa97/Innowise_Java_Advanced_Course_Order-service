package com.innowise.orderservice.controller;

import com.innowise.orderservice.service.dto.order.OrderCreateRequestDto;
import com.innowise.orderservice.service.dto.order.OrderResponseDto;
import com.innowise.orderservice.service.dto.order.OrderUpdateRequestDto;
import com.innowise.orderservice.service.interfaces.OrderService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.innowise.orderservice.utils.GlobalConstants.*;

/**
 * REST controller for managing order entities.
 * Provides endpoints for retrieving, creating, updating, and deleting orders.
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService<OrderResponseDto, OrderCreateRequestDto,
            OrderUpdateRequestDto, Long> orderService;

    /**
     * Retrieves orders based on the provided date and status criteria.
     *
     * @param dates    the date in format YYYY-MM-DD:YYYY-MM-DD where fist
     *                 value is 'from date' and second is 'to date' exclusive
     *                 for filtering orders; must not be null
     * @param status   the status for filtering orders; must not be null
     * @param pageable the pagination information; must not be null
     * @return a page of order response DTOs matching the specified criteria
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<OrderResponseDto>> getOrdersWithCriteria(
            @RequestParam(required = false)
            @Pattern(regexp = DATE_FORMAT,
                    message = "Date range must be in format YYYY-MM-DD:YYYY-MM-DD")
            String dates,
            @RequestParam(required = false)
            List<String> status,
            Pageable pageable
    ) {
        Map<String, Object> criteria = new HashMap<>();
        if (dates != null) {
            String[] solitaryDates = dates.split(":");
            criteria.put(FROM_DATE, LocalDate.parse(solitaryDates[0]));
            criteria.put(TO_DATE, LocalDate.parse(solitaryDates[1]));
        }

        criteria.put(STATUS, status);
        return ResponseEntity.ok(orderService.readAll(criteria, pageable));
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
     *                      the order; must not be null and must be valid according to validation
     *                      constraints
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
     * @param id      the identifier of the order to update; must not be null
     * @param request the request DTO containing the update data; must not be null
     *                and must be valid according to validation constraints
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
}
