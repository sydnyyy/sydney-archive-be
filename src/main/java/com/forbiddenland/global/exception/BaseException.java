package com.forbiddenland.global.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCode errorCode, String detail) {
        super(detail == null
                ? errorCode.getMessage()
                : errorCode.getMessage() + " - " + detail);
        this.errorCode = errorCode;
    }
}
