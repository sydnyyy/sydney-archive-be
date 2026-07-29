package com.sydneyarchive.global.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {

    public CustomAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
