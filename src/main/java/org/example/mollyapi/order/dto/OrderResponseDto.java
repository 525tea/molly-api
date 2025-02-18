package org.example.mollyapi.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.example.mollyapi.order.entity.Order;
import org.example.mollyapi.order.entity.OrderDetail;
import org.example.mollyapi.order.type.CancelStatus;
import org.example.mollyapi.order.type.OrderStatus;
import org.example.mollyapi.payment.entity.Payment;
import org.example.mollyapi.payment.repository.PaymentRepository;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponseDto {
    private Long orderId;
    private String tossOrderId;
    private Long userId;
    private Long totalAmount;
    private Integer pointUsage;
    private Integer pointSave;
    private OrderStatus status;
    private CancelStatus cancelStatus;

    private String orderedAt;

    private Long paymentAmount;
    private String paymentStatus;
    private String paymentMethod;
    private String deliveryStatus;

    private List<OrderDetailDto> orderDetails;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public OrderResponseDto(Order order, List<OrderDetail> orderDetailList, PaymentRepository paymentRepository) {
        this.orderId = order.getId();
        this.tossOrderId = order.getTossOrderId();
        this.userId = order.getUser().getUserId();
        this.totalAmount = orderDetailList.stream()
                .mapToLong(detail -> detail.getPrice() * detail.getQuantity())
                .sum();
        this.pointUsage = order.getPointUsage();
        this.pointSave = order.getPointSave();
        this.status = order.getStatus();
        this.cancelStatus = order.getCancelStatus();
        this.orderedAt = order.getOrderedAt() != null ? order.getOrderedAt().format(FORMATTER) : null;
        this.paymentAmount = order.getPaymentAmount();
        this.deliveryStatus = order.getDelivery() != null ? order.getDelivery().getStatus().name() : null;

        // 주문에 대한 최신 결제 정보 조회
        List<Payment> payments = paymentRepository.findLatestPaymentByOrderId(order.getId(), PageRequest.of(0, 1));
        if (!payments.isEmpty()) {
            Payment payment = payments.get(0);
            this.paymentStatus = payment.getPaymentStatus().name();
            this.paymentMethod = payment.getPaymentType();
        } else {
            this.paymentStatus = null;
            this.paymentMethod = null;
        }

        // OrderDetail 정보 매핑
        this.orderDetails = orderDetailList.stream()
                .map(OrderDetailDto::new)
                .collect(Collectors.toList());

        System.out.println("OrderResponseDto - orderedAt: " + this.orderedAt);
    }

    @Getter
    public static class OrderDetailDto {
        private Long orderId;
        private Long productId;
        private Long itemId;
        private String brandName;
        private String productName;
        private String size;
        private Long price;
        private Long quantity;

        public OrderDetailDto(OrderDetail orderDetail) {
            this.orderId = orderDetail.getOrder().getId();
            this.productId = orderDetail.getProductItem().getProduct().getId();
            this.itemId = orderDetail.getProductItem().getId();
            this.brandName = orderDetail.getBrandName();
            this.productName = orderDetail.getProductItem().getProduct().getProductName();
            this.size = orderDetail.getProductItem().getSize();
            this.price = orderDetail.getPrice();
            this.quantity = orderDetail.getQuantity();
        }
    }
}