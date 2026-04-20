package com.innowise.orderservice.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDto(
        String id,
        Long orderId,
        Long userId,
        PaymentStatus status,
        LocalDateTime timestamp,
        BigDecimal paymentAmount
) {
    public enum PaymentStatus {
        SUCCESS,
        FAILED
    }
}
