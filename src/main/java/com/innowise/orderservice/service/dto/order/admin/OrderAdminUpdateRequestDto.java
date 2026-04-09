package com.innowise.orderservice.service.dto.order.admin;

import com.innowise.orderservice.repository.entity.OrderStatus;
import com.innowise.orderservice.service.dto.orderitem.OrderItemRequestDto;

import java.math.BigDecimal;
import java.util.List;

public record OrderAdminUpdateRequestDto(
        Long userId,
        OrderStatus status,
        BigDecimal totalPrice,
        List<OrderItemRequestDto> itemList
) {
}
