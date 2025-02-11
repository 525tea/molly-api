package org.example.mollyapi.payment.order.service;

import lombok.extern.slf4j.Slf4j;
import org.example.mollyapi.common.exception.CustomException;
import org.example.mollyapi.common.exception.error.impl.PaymentError;
import org.example.mollyapi.payment.order.entity.Order;
import org.example.mollyapi.payment.order.repository.orderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class OrderService {
    private final org.example.mollyapi.payment.order.repository.orderRepository orderRepository;

    public OrderService(orderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    private void changeOrderStatus(String status) {
        log.info("change order status to " + status);
    }

    public void successOrder(String tossOrderId) {
        changeOrderStatus("approved");
        log.info("success order " + tossOrderId);
    }
    public void failOrder(String tossOrderId) {
        changeOrderStatus("rejected");
        log.info("fail order " + tossOrderId);

    }
    public String saveOrder(Integer amount) {
        String tossOrderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setTossOrderId(tossOrderId);
        order.setAmount(amount);
        orderRepository.save(order);
        return tossOrderId;
    }
    public Order findOrderByTossOrderId(String tossOrderId) {
        return orderRepository.findByTossOrderId(tossOrderId)
                .orElseThrow(() -> new CustomException(PaymentError.ORDER_NOT_FOUND));
    }
}
