package com.memetitle.global.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final int code;
    private final String message;

    public NotFoundException(final ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}