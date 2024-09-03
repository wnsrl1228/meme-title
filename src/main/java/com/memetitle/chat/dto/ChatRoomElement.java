package com.memetitle.chat.dto;

import com.memetitle.chat.domain.ChatRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomElement {

    private Long id;
    private String name;
    private int memberCount;
    private int maxCapacity;
    private LocalDateTime createdAt;

    public static ChatRoomElement of(ChatRoom chatRoom) {
        return ChatRoomElement.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .memberCount(chatRoom.getMemberCount())
                .maxCapacity(chatRoom.getMaxCapacity())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}
