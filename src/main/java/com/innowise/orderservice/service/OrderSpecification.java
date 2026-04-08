package com.innowise.orderservice.service;

import com.innowise.orderservice.repository.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.innowise.orderservice.utils.GlobalConstants.*;

public class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> haveDate(Map<String, Object> criteria) {
        return (root, query, criteriaBuilder) -> {
            if (criteria == null || !criteria.containsKey(FROM_DATE) ||
                    !criteria.containsKey(TO_DATE)) return null;

            LocalDate fromDate = (LocalDate) criteria.get(FROM_DATE);
            LocalDate toDate = (LocalDate) criteria.get(TO_DATE);
            return criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDate),
                    criteriaBuilder.lessThan(root.get("createdAt"), toDate)
            );
        };
    }

    public static Specification<Order> haveStatus(Map<String, Object> criteria) {
        return (root, query, criteriaBuilder) -> {

            if (criteria == null || criteria.get(STATUS) == null) {
                return null;
            }

            List<String> statuses = (List<String>) criteria.get(STATUS);
            List<Predicate> predicateList = statuses.stream().map(s ->
                    criteriaBuilder.equal(
                            criteriaBuilder.lower(root.get("status")),
                            s.toLowerCase()
                    )
            ).toList();

            return criteriaBuilder.or(predicateList);
        };
    }
}
