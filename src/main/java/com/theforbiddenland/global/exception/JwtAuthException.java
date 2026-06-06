package com.theforbiddenland.global.exception;

public class JwtAuthException extends BaseException {
    public JwtAuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
