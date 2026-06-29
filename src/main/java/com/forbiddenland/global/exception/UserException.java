package com.forbiddenland.global.exception;

public class UserException extends BaseException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
