package org.example.mollyapi.common.exception;

import lombok.Getter;
import org.example.mollyapi.common.exception.error.CustomError;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException{

    private HttpStatus httpStatus;
    private String message;

    public CustomException(CustomError customError) {
        super(customError.getMessage());
        this.message = customError.getMessage();
        this.httpStatus = customError.getHttpStatus();

    }
}
