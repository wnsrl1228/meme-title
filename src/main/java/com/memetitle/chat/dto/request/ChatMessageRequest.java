package com.memetitle.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequest {

    private String nickname;
    private String message;

    public ChatMessageRequest(String nickname, String message) {
        this.nickname = nickname;
        this.message = message;
    }
}