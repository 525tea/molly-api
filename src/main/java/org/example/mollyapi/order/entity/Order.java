package org.example.mollyapi.order.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.mollyapi.order.type.CancelStatus;
import org.example.mollyapi.order.type.OrderStatus;
import org.example.mollyapi.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    @Column(nullable = false)
    private Long paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private CancelStatus cancelStatus;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    @Builder
    public Order(User user, Long paymentAmount, OrderStatus status) {
        this.user = user;
        this.paymentAmount = paymentAmount;
        this.status = status;
        this.cancelStatus = CancelStatus.NONE;
        this.orderedAt = LocalDateTime.now();
        this.expirationTime = LocalDateTime.now().plusMinutes(10);
    }

    public void markAsFailed() {
        this.status = OrderStatus.FAILED;
    }

    public void markAsCanceled(String reason) {
        this.status = OrderStatus.CANCELED;
        this.cancelStatus = CancelStatus.REQUESTED;
    }

    public void setPaymentAmount(long totalAmount) {
        this.paymentAmount = totalAmount;
    }
}