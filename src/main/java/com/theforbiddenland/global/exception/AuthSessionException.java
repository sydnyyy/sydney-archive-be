package com.theforbiddenland.global.exception;

public class AuthSessionException extends BaseException {

    public AuthSessionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
