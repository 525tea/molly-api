package org.example.mollyapi.payment.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record OrderReqDto(
        @Schema(description = "상품리스트", example = "[]")
        List<String> productList,
        @Schema(description = "금액", example = "50000")
        Integer amount
) {
}
