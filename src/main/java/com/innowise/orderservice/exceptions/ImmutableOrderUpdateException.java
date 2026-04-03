package com.innowise.orderservice.exceptions;

public class ImmutableOrderUpdateException extends RuntimeException {

    private static final String UPDATE_ERROR_MSG = "Order is in immutable state.";

    public ImmutableOrderUpdateException() {
        super(UPDATE_ERROR_MSG);
    }
}
