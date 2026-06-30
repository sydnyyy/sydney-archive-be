package com.sydneyarchive.global.exception;

public class SseException extends BaseException {

    public SseException(ErrorCode errorCode) {
        super(errorCode);
    }
}
