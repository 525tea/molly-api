package org.example.mollyapi.payment.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.mollyapi.common.config.WebClientUtil;
import org.example.mollyapi.common.exception.CustomException;
import org.example.mollyapi.payment.dto.request.TossConfirmReqDto;
import org.example.mollyapi.payment.dto.response.TossPaymentResDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class PaymentWebClientUtil {
    private final WebClientUtil webClientUtil;
    @Value("${secret.confirm-url}")
    private String confirmUrl;
    public PaymentWebClientUtil(WebClientUtil webClientUtil) {
        this.webClientUtil = webClientUtil;
    }

    public TossPaymentResDto confirmPayment(TossConfirmReqDto request, String apiKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString((apiKey + ":").getBytes()));
        headers.put("Content-Type", "application/json");

        try {
            ResponseEntity<TossPaymentResDto> response = webClientUtil.post(
                    confirmUrl,
                    request,
                    TossPaymentResDto.class,
                    headers
            );

            return response.getBody();
        } catch (WebClientResponseException e) {
            handlePaymentError(e);
            throw e;
        }
    }

    private void handlePaymentError(WebClientResponseException e) {
        String errorBody = e.getResponseBodyAsString();
        HttpStatusCode status = e.getStatusCode();

        log.error("[결제 오류] Status={}, Response={}", status, errorBody);
        String errorMessage = extractErrorMessage(errorBody);
        String responseMessage = errorMessage != null ? errorMessage : "결제 요청 중 오류가 발생했습니다.";

        // 토스페이먼츠 오류는 동적으로 처리
        throw new CustomException(status, responseMessage);
    }

    private String extractErrorMessage(String errorBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(errorBody);
            return jsonNode.has("message") ? jsonNode.get("message").asText() : null;
        } catch (Exception ex) {
            return null; // JSON 파싱 실패 시 기본값 반환
        }
    }

//    private boolean shouldRetry(HttpStatusCode status) {
//        return status.is5xxServerError() // 5xx 서버 오류 (500, 502, 503, 504 등)
//                || status == HttpStatus.TOO_MANY_REQUESTS // 429: 요청 너무 많음
//                || status == HttpStatus.BAD_GATEWAY // 502: 게이트웨이 오류
//                || status == HttpStatus.SERVICE_UNAVAILABLE; // 503: 서비스 불가
//    }
}
