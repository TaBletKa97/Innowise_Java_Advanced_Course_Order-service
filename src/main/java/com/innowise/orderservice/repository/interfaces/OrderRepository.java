package com.innowise.orderservice.repository.interfaces;

import com.innowise.orderservice.repository.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Override
    @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
    List<Order> findAll();

    @Override
    @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
    Optional<Order> findById(Long id);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    boolean existsUserIdById(Long orderId, Long userId);

    List<Order> findOrderByUserId(Long userId);

}
