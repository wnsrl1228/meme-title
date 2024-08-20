package com.memetitle.chat.service;

import com.memetitle.chat.domain.ChatRoom;
import com.memetitle.chat.dto.response.ChatRoomsResponse;
import com.memetitle.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomsResponse getChatRooms() {

        final List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return ChatRoomsResponse.ofChatRooms(chatRooms);
    }
}
