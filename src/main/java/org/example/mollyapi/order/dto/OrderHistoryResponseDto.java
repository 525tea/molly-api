package org.example.mollyapi.order.dto;

import lombok.Getter;
import org.example.mollyapi.order.entity.Order;
import org.example.mollyapi.payment.entity.Payment;
import org.example.mollyapi.payment.repository.PaymentRepository;
import org.example.mollyapi.user.entity.User;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderHistoryResponseDto {
    private Long userId;
    private String userName;
    private Integer userPoint;
    private List<OrderSummaryDto> orders;

    public OrderHistoryResponseDto(User user, List<Order> orders, PaymentRepository paymentRepository) {
        this.userId = user.getUserId();
        this.userName = user.getName();
        this.userPoint = user.getPoint();
        this.orders = orders.stream()
                .map(order -> new OrderSummaryDto(order, paymentRepository))
                .collect(Collectors.toList());
    }

    @Getter
    public static class OrderSummaryDto {
        private Long orderId;
        private String tossOrderId;
        private Long totalAmount;
        private String deliveryStatus;
        private String paymentStatus;
        private String orderedAt;

        public OrderSummaryDto(Order order, PaymentRepository paymentRepository) {
            this.orderId = order.getId();
            this.tossOrderId = order.getTossOrderId();
            this.totalAmount = order.getTotalAmount();
            this.deliveryStatus = order.getDelivery() != null ? order.getDelivery().getStatus().name() : null;

            // 주문에 대한 최신 결제 정보 조회
            List<Payment> payments = paymentRepository.findLatestPaymentByOrderId(order.getId(), PageRequest.of(0, 1));
            this.paymentStatus = payments.isEmpty() ? null : payments.get(0).getPaymentStatus().name();

            this.orderedAt = order.getOrderedAt() != null ? order.getOrderedAt().toString() : null;
        }
    }
}