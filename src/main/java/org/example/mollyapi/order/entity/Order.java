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
@AllArgsConstructor
@Builder(toBuilder = true) // ✅ 빌더 설정을 여기에 유지
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id; // pk

    @Column(name = "toss_order_id",unique = true, length = 30)
    private String tossOrderId; // 결제용 주문 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    @Column(nullable = false)
    private Long totalAmount; // 포인트 적용 전

    @Column(nullable = true)
    private Long paymentAmount; // 결제 예정 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CancelStatus cancelStatus;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    @PrePersist
    protected void onCreate() {
        this.orderedAt = LocalDateTime.now();
        this.expirationTime = this.orderedAt.plusMinutes(10);
    }

    public void updateOrderedAt(LocalDateTime paymentTime) { // 결제 후 주문 일시 업데이트
        this.orderedAt = paymentTime;
    }

    public void markAsFailed() {
        this.status = OrderStatus.FAILED;
    }

    public void markAsCanceled(String reason) {
        this.status = OrderStatus.CANCELED;
        this.cancelStatus = CancelStatus.REQUESTED;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

//    public void setOrderNumber(String orderNumber) {
//        if (this.orderNumber == null) {
//            this.orderNumber = orderNumber;
//        } else {
//            throw new IllegalStateException("주문번호는 한 번만 설정할 수 있습니다.");
//        }
//    }
}