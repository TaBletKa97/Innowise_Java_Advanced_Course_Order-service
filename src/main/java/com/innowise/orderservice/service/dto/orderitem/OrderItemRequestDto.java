package com.innowise.orderservice.service.dto.orderitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDto(
        @NotNull
        @Positive
        Long itemId,

        @NotNull
        @Positive
        Integer quantity
) {
}
