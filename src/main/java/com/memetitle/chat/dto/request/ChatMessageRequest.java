package com.memetitle.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequest {

    private String nickname;
    private String message;
    private String date;

    public ChatMessageRequest(String nickname, String message, String date) {
        this.nickname = nickname;
        this.message = message;
        this.date = date;
    }
}