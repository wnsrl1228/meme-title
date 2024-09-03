package com.memetitle.chat.dto.response;

import com.memetitle.chat.domain.ChatRoom;
import com.memetitle.chat.dto.ChatRoomElement;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatRoomsResponse {
    private List<ChatRoomElement> chatRooms;

    public static ChatRoomsResponse ofChatRooms(List<ChatRoom> chatRooms) {
        final List<ChatRoomElement> chatRoomElements = chatRooms.stream()
                .map(ChatRoomElement::of)
                .toList();

        return ChatRoomsResponse.builder()
                .chatRooms(chatRoomElements)
                .build();
    }
}