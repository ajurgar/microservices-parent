package com.programmingPractise.orderservice.repository;

import com.programmingPractise.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
