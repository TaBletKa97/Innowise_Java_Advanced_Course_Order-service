package com.innowise.orderservice.service.dto.order.admin;

import com.innowise.orderservice.repository.entity.OrderStatus;
import com.innowise.orderservice.service.dto.orderitem.OrderItemRequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record OrderAdminCreateRequestDto(
        @NotNull
        @Positive
        Long userId,

        @NotBlank
        OrderStatus status,

        @NotNull
        @Positive
        BigDecimal totalPrice,

        @NotNull
        List<OrderItemRequestDto> itemList
) {
}
