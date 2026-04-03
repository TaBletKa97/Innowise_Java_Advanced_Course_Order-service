package com.innowise.orderservice.security;

public interface SecurityService {
    boolean canAccessOrder(Long orderId);
}
