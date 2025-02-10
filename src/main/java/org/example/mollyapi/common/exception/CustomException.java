package org.example.mollyapi.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.mollyapi.common.exception.error.CustomError;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{

    private HttpStatus httpStatus;
    private String message;

    public CustomException(CustomError customError) {
        super(customError.getMessage());
        this.message = customError.getMessage();
        this.httpStatus = customError.getStatus();

    }
}
