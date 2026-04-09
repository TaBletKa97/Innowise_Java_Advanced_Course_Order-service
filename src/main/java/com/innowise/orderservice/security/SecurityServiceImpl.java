package com.innowise.orderservice.security;

import com.innowise.orderservice.repository.interfaces.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("ssi")
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final OrderRepository orderRepository;

    @Override
    public boolean canAccessOrder(Long orderId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderRepository.existsUserIdById(orderId, userId);
    }
}
