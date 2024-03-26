package com.memetitle.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {


    INVALID_REQUEST(1000, "잘못된 요청입니다."),
    INVALID_AUTHORIZATION_CODE(4001, "유효하지 않은 인가 코드입니다."),
    INVALID_TOKEN(4002, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(4003, "만료된 토큰입니다."),
    INVALID_ID_TOKEN(4021, "유효하지 않은 ID 토큰입니다."),
    EXPIRED_ID_TOKEN(4022, "만료된 ID 토큰입니다."),
    FAILED_TO_RETRIEVE_PUBLIC_KEY_LIST(4023, "공개키 목록 조회에 실패하였습니다."),
    INVALID_KEY_SPEC(4024, "공개키를 생성할 수 없습니다."),

    INVALID_JWT_FORMAT(5001, "유효하지 않은 JWT 형식입니다."),

    SERVER_ERROR(9999, "서버에 문제가 발생하였습니다.");


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
