package com.innowise.orderservice.service.dto.userserviceresponce;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CardResponseDto(
        Long id,
        Long userId,
        String number,
        String holder,
        LocalDate expirationDate,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}