package org.example.mollyapi.common.exception.error;

import org.springframework.http.HttpStatus;

public interface CustomError {

    String getMessage();

    HttpStatus getStatus();
}

