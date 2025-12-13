package com.wishlist.global.exception;

public class DuplicateReadingSessionException extends BaseException {
    public DuplicateReadingSessionException() {
        super(ErrorCode.DUPLICATE_READING_SESSION);
    }
}
