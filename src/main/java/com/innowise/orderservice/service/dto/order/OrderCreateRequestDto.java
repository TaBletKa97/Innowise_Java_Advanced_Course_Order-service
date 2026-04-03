package com.innowise.orderservice.service.dto.order;

import com.innowise.orderservice.service.dto.orderitem.OrderItemRequestDto;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreateRequestDto(

        @NotNull
        List<OrderItemRequestDto> itemList
) {
}
