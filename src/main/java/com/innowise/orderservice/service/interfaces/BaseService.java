package com.innowise.orderservice.service.interfaces;

import java.util.List;

public interface BaseService<S, C, U, I> {

    List<S> readAll();

    S readById(I id);

    S create(C createRequest);

    S update(I id, U updateRequest);

    void deleteById(I id);
}