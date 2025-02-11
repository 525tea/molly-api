package org.example.mollyapi.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Builder
public record PaymentConfirmReqDto(

        @Schema(description = "토스 주문 ID", example = "MC4zNjI2OTUyNDQ5ODQ4")
        String tossOrderId,
        @Schema(description = "토스 결제 KEY", example = "tgen_20250210170102TeTf1")
        String paymentKey,
        @Schema(description = "결제금액", example = "50000")
        Integer amount,
        @Schema(description = "결제수단", example = "card")
        String paymentType,
        @Schema(description = "사용한 포인트", example = "5000")
        Integer point,
        @Schema(description = "배송지 id", example = "1")
        Long deliveryId
)
{}