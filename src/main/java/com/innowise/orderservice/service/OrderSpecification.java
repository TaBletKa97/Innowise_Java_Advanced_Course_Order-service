package com.innowise.orderservice.service;

import com.innowise.orderservice.repository.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> haveDate(String date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) return null;
            LocalDate day = LocalDate.parse(date);
            LocalDate nextDay = day.plusDays(1);
            return criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), day),
                    criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), nextDay)
            );
        };
    }

    public static Specification<Order> haveStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isBlank()) return null;
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("status")),
                    status.toLowerCase()
            );
        };
    }
}
