package com.wishlist.global.exception;

public class JwtCreationException extends BaseException {

    public JwtCreationException(String detail) {
        super(ErrorCode.JWT_CREATION_FAILED, detail);
    }

    public JwtCreationException() {
        super(ErrorCode.JWT_CREATION_FAILED);
    }
}
