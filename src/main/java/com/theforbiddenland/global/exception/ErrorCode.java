package com.theforbiddenland.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR("CM001", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류"),
    INVALID_REQUEST("CM002", HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),

    // ReadingSession
    DUPLICATE_READING_SESSION("RS001", HttpStatus.CONFLICT, "이미 존재하는 독서 세션입니다 (title + author 중복)"),

    // JWT
    JWT_CREATION_FAILED("JT001", HttpStatus.INTERNAL_SERVER_ERROR, "JWT 생성에 실패했습니다"),

    // auth
    UNSUPPORTED_PROVIDER("AU001", HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 공급자입니다."),
    NOT_AN_ADMIN("AU002", HttpStatus.FORBIDDEN, "허용된 관리자만 접근 가능합니다."),

    // User
    USER_NOT_FOUND("UR001", HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다"),
    SID_CREATION_FAILED("UR002", HttpStatus.INTERNAL_SERVER_ERROR, "SID 생성 중 오류가 발생했습니다."),
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