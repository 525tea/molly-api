package org.example.mollyapi.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.example.mollyapi.order.entity.Order;
import org.example.mollyapi.order.entity.OrderDetail;
import org.example.mollyapi.order.type.CancelStatus;
import org.example.mollyapi.order.type.OrderStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponseDto {
    private String orderId;
    private Long userId;
    private Long totalAmount;
    private OrderStatus status;
    private CancelStatus cancelStatus;

    private String orderedAt;
    private List<OrderDetailDto> orderDetails;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public OrderResponseDto(Order order, List<OrderDetail> orderDetailList) {
        this.orderId = "ORD-" + order.getId();
        this.userId = order.getUser().getUserId();
        this.totalAmount = orderDetailList.stream()
                .mapToLong(detail -> detail.getPrice() * detail.getQuantity())
                .sum();
        this.status = order.getStatus();
        this.cancelStatus = order.getCancelStatus();
        this.orderedAt = order.getOrderedAt() != null ? order.getOrderedAt().format(FORMATTER) : null;
        this.orderDetails = orderDetailList.stream()
                .map(OrderDetailDto::new)
                .collect(Collectors.toList());

        System.out.println("OrderResponseDto - orderedAt: " + this.orderedAt);
    }

    @Getter
    public static class OrderDetailDto {
        private Long productId;
        private Long itemId;
        private String productName;
        private Long price;
        private Long quantity;

        public OrderDetailDto(OrderDetail orderDetail) {
            this.productId = orderDetail.getProductItem().getProduct().getId();
            this.itemId = orderDetail.getProductItem().getId();
            this.productName = orderDetail.getProductItem().getProduct().getProductName();
            this.price = orderDetail.getPrice();
            this.quantity = orderDetail.getQuantity();
        }
    }
}