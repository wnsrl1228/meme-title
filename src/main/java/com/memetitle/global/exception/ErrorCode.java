package com.memetitle.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {


    INVALID_REQUEST(1000, "잘못된 요청입니다."),

    NOT_FOUND_MEMBER_ID(2000, "해당 멤버를 찾을 수 없습니다."),
    NOT_FOUND_MEME_ID(2001, "해당 밈을 찾을 수 없습니다."),
    NOT_FOUND_TITLE_ID(2002, "해당 제목을 찾을 수 없습니다."),
    NOT_FOUND_COMMENT_ID(2003, "해당 댓글을 찾을 수 없습니다."),
    NOT_FOUND_TITLE_LIKE(2004, "해당 제목에 좋아요가 없습니다."),
    NOT_FOUND_COMMENT_LIKE(2005, "해당 댓글에 좋아요가 없습니다."),
    TITLE_ACCESS_DENIED(2012, "해당 제목에 대한 권한이 없습니다."),
    COMMENT_ACCESS_DENIED(2013, "해당 댓글에 대한 권한이 없습니다."),

    DUPLICATE_NICKNAME(3000, "이미 존재하는 닉네임입니다."),
    DUPLICATE_TITLE_LIKE(3002, "이미 좋아요한 제목입니다."),
    DUPLICATE_COMMENT_LIKE(3003, "이미 좋아요한 댓글입니다."),
    SELF_TITLE_LIKE_DISALLOWED(4002, "본인 제목에는 좋아요를 할 수 없습니다."),
    SELF_COMMENT_LIKE_DISALLOWED(4003, "본인 댓글에는 좋아요를 할 수 없습니다."),

    INVALID_AUTHORIZATION_CODE(4001, "유효하지 않은 인가 코드입니다."),
    INVALID_TOKEN(4002, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(4003, "만료된 토큰입니다."),
    INVALID_ID_TOKEN(4021, "유효하지 않은 ID 토큰입니다."),
    EXPIRED_ID_TOKEN(4022, "만료된 ID 토큰입니다."),
    FAILED_TO_RETRIEVE_PUBLIC_KEY_LIST(4023, "공개키 목록 조회에 실패하였습니다."),
    INVALID_KEY_SPEC(4024, "공개키를 생성할 수 없습니다."),

    INVALID_JWT_FORMAT(5001, "유효하지 않은 JWT 형식입니다."),

    FAILED_TO_STORE_FILE(8000, "이미지가 저장에 실패했습니다."),
    EMPTY_FILE_STORE_FAILED(8001, "이미지가 비어있습니다."),
    OUTSIDE_CURRENT_DIRECTORY(8002, "현재 디렉토리에 저장할 수 없습니다."),
    NOT_FOUND_IMAGE(8003, "이미지를 찾을 수 없습니다."),
    SERVER_ERROR(9999, "서버에 문제가 발생하였습니다.");


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
