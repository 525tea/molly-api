package org.example.mollyapi.common.exception.error.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.mollyapi.common.exception.error.CustomError;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CategoryError implements CustomError {
    INVALID_CATEGORY(HttpStatus.FORBIDDEN, "해당 카테고리는 존재하지 않습니다.")
    ;

    private final HttpStatus status;
    private final String message;
}