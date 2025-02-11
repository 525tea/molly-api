package org.example.mollyapi.payment.order.repository;

import org.example.mollyapi.payment.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface orderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByTossOrderId(String orderId);
}
