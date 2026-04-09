package com.innowise.orderservice.exceptions;

public class ItemNotFoundException extends RuntimeException {

    private static final String NO_ITEM_MSG = "Item with id %d not found";

    public ItemNotFoundException(Long id) {
        super(String.format(NO_ITEM_MSG, id));
    }
}
