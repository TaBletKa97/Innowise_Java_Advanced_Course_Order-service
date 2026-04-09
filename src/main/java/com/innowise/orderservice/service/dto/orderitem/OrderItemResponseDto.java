package com.innowise.orderservice.service.dto.orderitem;

import com.innowise.orderservice.service.dto.item.ItemResponseDto;

import java.time.LocalDateTime;

public record OrderItemResponseDto(
        Long id,
        Integer quantity,
        ItemResponseDto item,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
