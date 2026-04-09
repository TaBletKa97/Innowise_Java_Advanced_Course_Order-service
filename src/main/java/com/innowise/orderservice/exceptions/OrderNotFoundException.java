package com.innowise.orderservice.exceptions;

public class OrderNotFoundException extends RuntimeException {

    private static final String NO_ORDER_MSG = "Order with id %d is not exist.";

    public OrderNotFoundException(Long id) {
        super(String.format(NO_ORDER_MSG, id));
    }
}
