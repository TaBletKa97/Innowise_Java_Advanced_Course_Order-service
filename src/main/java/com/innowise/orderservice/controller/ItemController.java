package com.innowise.orderservice.controller;

import com.innowise.orderservice.service.dto.item.ItemCreateRequestDto;
import com.innowise.orderservice.service.dto.item.ItemResponseDto;
import com.innowise.orderservice.service.dto.item.ItemUpdateRequestDto;
import com.innowise.orderservice.service.interfaces.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing items.
 * Provides endpoints to retrieve, create, update, and delete items.
 * Access to creation, update, and deletion operations is restricted to users
 * with the ADMIN authority.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService<ItemResponseDto, ItemCreateRequestDto,
            ItemUpdateRequestDto, Long> itemService;

    /**
     * Retrieves all items.
     *
     * @return a list of all items; may be empty but never null
     */
    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllItems() {
        return ResponseEntity.ok(itemService.readAll());
    }

    /**
     * Retrieves an item by its identifier.
     *
     * @param id the identifier of the item to retrieve; may not be null
     * @return the item with provided id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.readById(id));
    }

    /**
     * Creates a new item.
     *
     * @param createRequest the DTO containing the item creation data;
     * must be valid and non-null
     * @return the response DTO representing the created item
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ItemResponseDto> createItem(
            @RequestBody @Validated ItemCreateRequestDto createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.create(createRequest));
    }

    /**
     * Updates an existing item identified by the given identifier using the
     * provided update request data.
     *
     * @param id the identifier of the item to update; must be valid and non-null
     * @param value the DTO containing the item update data; must be valid and non-null
     * @return the response DTO representing the updated item
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ItemResponseDto> updateItem(
            @PathVariable Long id,
            @RequestBody @Validated ItemUpdateRequestDto value
    ) {
        return ResponseEntity.ok(itemService.update(id, value));
    }

    /**
     * Deletes an item identified by the given identifier.
     *
     * @param id the identifier of the item to delete; may not be null
     * @return an empty response with status 204 (No Content) on successful deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
