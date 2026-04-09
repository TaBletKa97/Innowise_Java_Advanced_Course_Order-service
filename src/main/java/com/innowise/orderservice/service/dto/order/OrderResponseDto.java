package com.innowise.orderservice.service.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innowise.orderservice.repository.entity.OrderStatus;
import com.innowise.orderservice.service.dto.orderitem.OrderItemResponseDto;
import com.innowise.orderservice.service.dto.userserviceresponce.UserResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
        Long id,
        OrderStatus status,
        BigDecimal totalPrice,
        Boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UserResponseDto user,
        List<OrderItemResponseDto> itemList,
        @JsonIgnore
        Long userId
) {
}
