package com.theforbiddenland.global.exception;

import com.theforbiddenland.global.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({LoginSessionException.class, JwtAuthException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(BaseException e) {
        log.error("[{}] errorCode={}, message={}",
                e.getClass().getSimpleName(),
                e.getErrorCode().getCode(), e.getErrorCode().getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .code(String.valueOf(e.getErrorCode().getCode()))
                .message("다시 로그인해 주세요.")
                .status(e.getErrorCode().getStatus().value())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        log.error("[BaseException] errorCode={}, message={}",
                e.getErrorCode().getCode(), e.getErrorCode().getMessage());

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
        log.error("[Exception] message={}", e.getMessage());

        /*
            SSE 연결이 끊어진 후 emitter.send() 실패하면
            Spring Async 종료 과정에서 AsyncRequestNotUsableException 발생
            이미 응답을 전송할 수 없는 상태이므로 별도의 예외 응답을 생성하지 않음
         */
        if (e instanceof AsyncRequestNotUsableException) {
            return null;
        }

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
