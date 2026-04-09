package com.innowise.orderservice.service.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ItemCreateRequestDto(

        @NotBlank
        @Size(min = 2, max = 100)
        String name,

        @NotNull
        @Positive
        BigDecimal price

) {
}
