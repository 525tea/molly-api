package org.example.mollyapi.order.dto;

import lombok.Getter;
import org.example.mollyapi.delivery.dto.DeliveryResponseDto;
import org.example.mollyapi.order.entity.Order;
import org.example.mollyapi.order.entity.OrderDetail;
import org.example.mollyapi.order.type.CancelStatus;
import org.example.mollyapi.order.type.OrderStatus;
import org.example.mollyapi.payment.dto.response.PaymentInfoResDto;
import org.example.mollyapi.payment.entity.Payment;
import org.example.mollyapi.payment.repository.PaymentRepository;
import org.springframework.data.domain.PageRequest;

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

    private PaymentInfoResDto payment;

    //    private String deliveryStatus;
    private DeliveryResponseDto delivery;
    private List<OrderDetailResponseDto> orderDetails;

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

        // 주문에 대한 최신 결제 정보 조회
        List<Payment> payments = paymentRepository.findLatestPaymentByOrderId(order.getId(), PageRequest.of(0, 1));
        if (!payments.isEmpty()) {
            this.payment = PaymentInfoResDto.from(payments.get(0));
        } else {
            this.payment = null;
        }

        // 배송 정보
        this.delivery = order.getDelivery() != null ? DeliveryResponseDto.from(order.getDelivery()) : null;

        // OrderDetail 리스트 변환
        this.orderDetails = orderDetailList.stream()
                .map(OrderDetailResponseDto::from)
                .collect(Collectors.toList());
    }

    public static OrderResponseDto from(Order order, PaymentRepository paymentRepository) {
        return new OrderResponseDto(order, order.getOrderDetails(), paymentRepository);
    }
}