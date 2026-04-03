package com.innowise.orderservice.service.interfaces;

import com.innowise.orderservice.service.dto.order.admin.OrderAdminCreateRequestDto;
import com.innowise.orderservice.service.dto.order.admin.OrderAdminUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface OrderService<S, C, U, I> extends BaseService<S, C, U, I> {
    S adminCreate(OrderAdminCreateRequestDto request);
    S adminUpdate(Long id, OrderAdminUpdateRequestDto request);
    List<S> readByUserId(Long userId);
    Page<S> readAll(Map<String, String> criteria, Pageable pageable);
}
