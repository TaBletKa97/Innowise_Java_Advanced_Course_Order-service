package com.innowise.orderservice.service.dto.item;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemResponseDto(
        Long id,
        String name,
        BigDecimal price,
        Boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
