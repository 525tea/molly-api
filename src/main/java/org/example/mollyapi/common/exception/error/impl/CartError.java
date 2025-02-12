package org.example.mollyapi.common.exception.error.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.mollyapi.common.exception.error.CustomError;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CartError implements CustomError {

    MAX_CART(HttpStatus.BAD_REQUEST, "장바구니 최대 수량을 초과했습니다."),
    EMPTY_CART(HttpStatus.NO_CONTENT, "장바구니가 비었습니다."),
    FAIL_UPDATE(HttpStatus.INTERNAL_SERVER_ERROR, "변경 사항 업데이트에 실패했습니다.")
    ;

    private final HttpStatus status;
    private final String message;

}
