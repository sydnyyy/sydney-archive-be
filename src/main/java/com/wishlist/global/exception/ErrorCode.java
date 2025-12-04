package com.wishlist.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR("CM001", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류"),
    INVALID_REQUEST("CM002", HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),

    // ReadingSession
    DUPLICATE_READING_SESSION("RS001", HttpStatus.CONFLICT, "이미 존재하는 독서 세션입니다 (title + author 중복)"),

    ;

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String getCode() { return code; }
    public HttpStatus getStatus() { return status; }
    public String getMessage() { return message; }
}