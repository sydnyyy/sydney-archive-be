package com.forbiddenland.global.exception;

public class SseException extends BaseException {

    public SseException(ErrorCode errorCode) {
        super(errorCode);
    }
}
