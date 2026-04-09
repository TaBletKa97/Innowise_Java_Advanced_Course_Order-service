package com.innowise.orderservice.service.dto.item;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ItemUpdateRequestDto(

        @Size(min = 2, max = 100)
        String name,

        @Positive
        BigDecimal price

) {
}
