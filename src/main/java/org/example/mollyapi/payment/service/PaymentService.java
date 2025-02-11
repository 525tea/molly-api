package org.example.mollyapi.payment.service;

import org.example.mollyapi.payment.dto.response.PaymentResDto;
import org.example.mollyapi.payment.entity.Payment;

public interface PaymentService {

    public Payment processPayment(Long userId, String paymentKey, String tossOrderId, Integer amount, String paymentType);

    public void cancelPayment();

    public void successPayment(Payment payment, String tossOrderId);

    public void failPayment(Payment payment, String tossOrderId, String failureReason);


}
