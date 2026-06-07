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
    ACCESS_TOKEN_MISSING("JT002", HttpStatus.UNAUTHORIZED, "액세스 토큰이 존재하지 않습니다."),
    ACCESS_TOKEN_EXPIRED("JT003", HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다."),
    REFRESH_TOKEN_MISSING("JT004", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 존재하지 않습니다."),
    JWT_CLAIM_MISSING("JT005", HttpStatus.BAD_REQUEST, "토큰 필수 정보가 누락되었습니다."),
    INVALID_TOKEN("JT006", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_EXPIRED("JT007", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),

    // auth
    UNSUPPORTED_PROVIDER("AU001", HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 공급자입니다."),
    NOT_AN_ADMIN("AU002", HttpStatus.FORBIDDEN, "허용된 관리자만 접근 가능합니다."),
    COOKIE_NOT_FOUND("AU003", HttpStatus.UNAUTHORIZED, "인증 쿠키가 존재하지 않습니다."),
    LOGIN_SESSION_ID_EXPIRED("AU004", HttpStatus.UNAUTHORIZED, "로그인 인증 세션이 만료되었습니다."),
    ACCESS_DENIED("AU005", HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // User
    USER_NOT_FOUND("UR001", HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다"),
    SID_CREATION_FAILED("UR002", HttpStatus.INTERNAL_SERVER_ERROR, "SID 생성 중 오류가 발생했습니다."),
    INVALID_ROLE_VALUE("UR003", HttpStatus.BAD_REQUEST, "정의되지 않은 권한 유형입니다."),

    // Item
    ITEM_NOT_FOUND("IT001", HttpStatus.NOT_FOUND, "존재하지 않는 아이템입니다"),
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