package com.innowise.orderservice.exceptions;

public class AlreadyDeletedException extends RuntimeException {

    public AlreadyDeletedException(Long id) {
        super(String.format("Entity with id %d was already deleted.", id));
    }
}
