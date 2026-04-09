package com.innowise.orderservice.repository.interfaces;

import com.innowise.orderservice.repository.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
