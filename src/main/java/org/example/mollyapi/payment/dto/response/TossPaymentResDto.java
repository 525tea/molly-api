package org.example.mollyapi.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public record TossPaymentResDto(
        String mId,
        String version,
        String paymentKey,
        String status,
        String lastTransactionKey,
        String method,
        String orderId,
        String orderName,
        Integer totalAmount,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        CardInfo card,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        EasyPayInfo easyPay
) {
    public record CardInfo(
            String issuerCode,
            String acquirerCode,
            String number,
            Integer installmentPlanMonths,
            Boolean isInterestFree,
            String interestPayer,
            String approveNo,
            Boolean useCardPoint,
            String cardType,
            String ownerType,
            String acquireStatus,
            Integer amount
    ) {}
    public record EasyPayInfo(
            String provider,
            Integer amount,
            Integer discountAmount
    ) {}
}