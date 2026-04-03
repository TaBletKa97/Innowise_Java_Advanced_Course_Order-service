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

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor

public class ItemController {

    private final ItemService<ItemResponseDto, ItemCreateRequestDto,
            ItemUpdateRequestDto, Long> itemService;

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllItems() {
        return ResponseEntity.ok(itemService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.readById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ItemResponseDto> createItem(
            @RequestBody @Validated ItemCreateRequestDto createRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.create(createRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ItemResponseDto> updateItem(
            @PathVariable Long id,
            @RequestBody @Validated ItemUpdateRequestDto value
    ) {
        return ResponseEntity.ok(itemService.update(id, value));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
