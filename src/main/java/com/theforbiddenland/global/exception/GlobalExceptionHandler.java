package com.theforbiddenland.global.exception;

import com.theforbiddenland.global.exception.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        ErrorCode code = e.getErrorCode();

        ErrorResponse response = ErrorResponse.builder()
                .code(code.getCode())
                .message(code.getMessage())
                .status(code.getStatus().value())
                .build();

        return ResponseEntity
                .status(code.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;

        ErrorResponse response = ErrorResponse.builder()
                .code(code.getCode())
                .message(code.getMessage())
                .status(code.getStatus().value())
                .build();

        return ResponseEntity
                .status(code.getStatus())
                .body(response);
    }
}
