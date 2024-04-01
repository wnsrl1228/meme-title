package com.memetitle.global.exception;

import lombok.Getter;

@Getter
public class StorageException extends RuntimeException {
    private final int code;
    private final String message;

    public StorageException(final ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}