package com.innowise.orderservice.messagebrockers;

import com.innowise.orderservice.exceptions.OrderNotFoundException;
import com.innowise.orderservice.repository.entity.OrderStatus;
import com.innowise.orderservice.repository.interfaces.OrderRepository;
import com.innowise.orderservice.service.dto.PaymentResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.innowise.orderservice.service.dto.PaymentResponseDto.PaymentStatus.SUCCESS;

@Log4j2
@Service
@RequiredArgsConstructor
public class PaymentKafkaConsumer {

    private final OrderRepository repository;

    @KafkaListener(topics = "CREATE_PAYMENT")
    public void consumePayment(PaymentResponseDto payment) {
        log.debug("Receive payment {} from kafka", payment);

        var order = repository.findById(payment.orderId()).orElseThrow(() ->
                new OrderNotFoundException(payment.orderId()));

        if (payment.paymentAmount().compareTo(order.getTotalPrice()) != 0) {
            order.setStatus(OrderStatus.SUSPICIOUS);
            repository.save(order);
            return;
        }

        if (payment.status().equals(SUCCESS)) {
            order.setStatus(OrderStatus.APPROVED);
        } else {
            order.setStatus(OrderStatus.REJECTED);
        }
        repository.save(order);
    }
}
