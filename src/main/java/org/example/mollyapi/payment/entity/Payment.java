package org.example.mollyapi.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.mollyapi.common.entity.Base;
import org.example.mollyapi.common.exception.CustomException;
import org.example.mollyapi.common.exception.error.impl.PaymentError;
import org.example.mollyapi.payment.type.PaymentStatus;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Payment extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String paymentType;

    @Column
    private Integer amount;

    @Column
    private String paymentKey;

    @Column
    private String tossOrderId;

    @Column
    private LocalDateTime paymentDate;

    @Column
    private String failureReason;

    @Column
    private Integer point;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "user_id")
    private Long userId;


    // 생성자 팩토리 메서드
    public static Payment from(Long userId, Long orderId, String tossOrderId, String paymentKey, String paymentType, Integer amount, String paymentStatus) {
        return Payment.builder()
                .userId(userId)
                .orderId(orderId)
                .tossOrderId(tossOrderId)
                .paymentKey(paymentKey)
                .paymentType(paymentType)
                .amount(amount)
                .paymentStatus(PaymentStatus.from(paymentStatus))
                .build();
    }

    public static Payment ready(Long userId,String tossOrderId, Integer amount) {
        return Payment.builder()
                .userId(userId)
                .tossOrderId(tossOrderId)
                .amount(amount)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }





    // 결제 성공 시 필드 추가 도메인 로직
    public void successPayment(Integer point) {
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new CustomException(PaymentError.PAYMENT_ALREADY_PROCESSED);
        }
        this.point = point;
        this.paymentStatus = PaymentStatus.APPROVED;
        this.paymentDate = LocalDateTime.now();
    }

//
    public void failPayment(String failureReason) {
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new CustomException(PaymentError.PAYMENT_ALREADY_PROCESSED);
        }
        this.paymentStatus = PaymentStatus.FAILED;
        this.failureReason = failureReason;
    }





}
