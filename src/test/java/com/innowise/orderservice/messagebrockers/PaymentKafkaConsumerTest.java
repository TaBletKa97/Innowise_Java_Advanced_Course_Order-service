package com.innowise.orderservice.messagebrockers;

import com.innowise.orderservice.exceptions.OrderNotFoundException;
import com.innowise.orderservice.repository.entity.Order;
import com.innowise.orderservice.repository.entity.OrderStatus;
import com.innowise.orderservice.repository.interfaces.OrderRepository;
import com.innowise.orderservice.service.dto.PaymentResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.innowise.orderservice.service.dto.PaymentResponseDto.PaymentStatus.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentKafkaConsumerTest {

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private PaymentKafkaConsumer consumer;


    @Test
    void consumePayment_shouldThrowOrderNotFoudException() {
        // Arrange
        var requestPayment = new PaymentResponseDto(
                "id",
                1L,
                1L,
                SUCCESS,
                LocalDateTime.now(),
                BigDecimal.TEN
        );

        when(repository.findById(requestPayment.orderId())).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(OrderNotFoundException.class, () ->
                consumer.consumePayment(requestPayment));
    }

    @Test
    void consumePayment_shouldSetSuspicious() {
        // Arrange
        var requestPayment = new PaymentResponseDto(
                "id",
                1L,
                1L,
                SUCCESS,
                LocalDateTime.now(),
                BigDecimal.TEN
        );

        Order order = new Order();
        order.setTotalPrice(BigDecimal.ONE);

        when(repository.findById(requestPayment.orderId()))
                .thenReturn(Optional.of(order));
        // Act
        consumer.consumePayment(requestPayment);

        // Assert
        assertEquals(OrderStatus.SUSPICIOUS, order.getStatus());
        verify(repository).save(order);
    }

    @ParameterizedTest
    @CsvSource({
            "SUCCESS, APPROVED",
            "FAILED, REJECTED"
    })
    void consumePayment_shouldSetCorrectStatus(
            PaymentResponseDto.PaymentStatus paymentStatus,
            OrderStatus orderStatus
    ) {
        // Arrange
        var requestPayment = new PaymentResponseDto(
                "id",
                1L,
                1L,
                paymentStatus,
                LocalDateTime.now(),
                BigDecimal.TEN
        );

        Order order = new Order();
        order.setTotalPrice(BigDecimal.TEN);

        when(repository.findById(requestPayment.orderId()))
                .thenReturn(Optional.of(order));
        // Act
        consumer.consumePayment(requestPayment);

        // Assert
        assertEquals(orderStatus, order.getStatus());
        verify(repository).save(order);
    }
}