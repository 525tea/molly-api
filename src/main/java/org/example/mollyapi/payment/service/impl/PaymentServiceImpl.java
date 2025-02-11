package org.example.mollyapi.payment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mollyapi.common.config.WebClientUtil;
import org.example.mollyapi.common.exception.CustomException;
import org.example.mollyapi.common.exception.error.impl.PaymentError;
import org.example.mollyapi.payment.order.entity.Order;
import org.example.mollyapi.payment.order.service.OrderService;
import org.example.mollyapi.payment.dto.request.TossConfirmReqDto;
import org.example.mollyapi.payment.dto.response.TossPaymentResDto;
import org.example.mollyapi.payment.entity.Payment;
import org.example.mollyapi.payment.repository.PaymentRepository;
import org.example.mollyapi.payment.service.PaymentService;
import org.example.mollyapi.payment.util.PaymentWebClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final WebClientUtil webClientUtil;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final PaymentWebClientUtil paymentWebClientUtil;

    @Value("${secret.payment-api-key}")
    private String apiKey;

    /*
        결제 로직
     */
    @Transactional
    public Payment processPayment(Long userId, String paymentKey, String tossOrderId, Integer amount, Integer point, Long deliveryId, String paymentType) {
        /* 1. find order with tossOrderId
         2. validate amount
         3. success/failure logic
         3-1 if failure -> throw exception
         4. create payment
         5. toss api
         6. success/failure logic
        */

        // order findByTossOrderId
        Order order = orderService.findOrderByTossOrderId(tossOrderId);
        Integer orderAmount = order.getAmount();

        // 포인트 검증

        // 결제정보 검증
        validateAmount(orderAmount, amount);

        // create payment -> 도메인 로직 ready 로 refactor
        Payment payment = Payment.from(userId, order.getId(), tossOrderId, paymentKey, paymentType, amount, "결제대기");

        // payment API
        TossConfirmReqDto tossConfirmReqDto = new TossConfirmReqDto(paymentKey, tossOrderId, amount);
        ResponseEntity<TossPaymentResDto> response = tossPaymentApi(tossConfirmReqDto, apiKey);

        // response 정합성 검사
        boolean res = validateResponse(response);

        // api 응답 tossResDto로 추출
        TossPaymentResDto tossResDto = response.getBody();

        // 결제 성공 및 실패 로직
        if (res) {
            successPayment(payment, tossOrderId, point);
        } else {
            failPayment(payment, tossOrderId, "실패");
        }
        paymentRepository.save(payment);
        return payment;
    }

    /*
        결제 취소
     */
    public void cancelPayment() {

    }

    /*
        결제 성공
     */
    public void successPayment(Payment payment, String tossOrderId, Integer point) {
        //payment status change
        payment.successPayment(point);
        //order status change
        orderService.successOrder(tossOrderId, point);
        // 포인트 차감 및 적립 로직
    }

    /*
        결제 실패
     */
    public void failPayment(Payment payment, String tossOrderId, String failureReason) {
        payment.failPayment(failureReason);
        orderService.failOrder(tossOrderId);
    }


    /*
        tossApi 호출
     */
    private ResponseEntity<TossPaymentResDto> tossPaymentApi(TossConfirmReqDto tossConfirmReqDto, String apiKey) {

        TossPaymentResDto tossPaymentResDto = paymentWebClientUtil.confirmPayment(tossConfirmReqDto,apiKey);
        return ResponseEntity.ok(tossPaymentResDto);
    }

    /*
        payment 생성
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    /*
        HTTP 응답 검증
     */
    private boolean validateResponse(ResponseEntity<TossPaymentResDto> response) {
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null;
    }

    /*
        결제 금액 검증
     */
    private void validateAmount(Integer orderAmount, Integer amount) {
        if (!Objects.equals(amount, orderAmount)) {
            throw new CustomException(PaymentError.PAYMENT_AMOUNT_MISMATCH);
        }
    }
}