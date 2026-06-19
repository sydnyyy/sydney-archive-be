package com.theforbiddenland.global.exception;

public class LoginSessionException extends BaseException {

    public LoginSessionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
