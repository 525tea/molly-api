package org.example.mollyapi.payment.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.mollyapi.common.dto.CommonResDto;
import org.example.mollyapi.common.exception.CustomErrorResponse;
import org.example.mollyapi.payment.order.dto.OrderReqDto;
import org.example.mollyapi.payment.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment/order")
@RequiredArgsConstructor
@Tag(name = "결제 Controller", description = "결제 담당")
public class OrderController {
    private final OrderService orderService;
    @Operation(summary = "주문정보 저장 api", description = "임시 주문 저장 api 입니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "400", description = "실패",
                    content = @Content(schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    @PostMapping("/save")
    public ResponseEntity<CommonResDto> createOrder(OrderReqDto orderReqDto) {
        String tossOrderId = orderService.saveOrder(orderReqDto.amount());
        return ResponseEntity.ok(new CommonResDto(tossOrderId));
    }
}
