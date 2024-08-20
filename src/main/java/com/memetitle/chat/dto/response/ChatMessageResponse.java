package com.memetitle.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageResponse {

    private String nickname;
    private String message;
}

