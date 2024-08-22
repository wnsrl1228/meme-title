package com.memetitle.chat.presentation;

import com.memetitle.chat.dto.request.ChatMessageRequest;
import com.memetitle.chat.dto.response.ChatMessageResponse;
import com.memetitle.chat.dto.response.ChatRoomsResponse;
import com.memetitle.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    /**
     * 클라이언트에서 메세지 요청시 : /pub/chat/message
     * 채팅방 사람들에게 메세지 전달 : /sub/chat/messages
     */
    @MessageMapping("/chat/{roomId}/message")
    @SendTo("/sub/chat/{roomId}/messages")
    public ResponseEntity<ChatMessageResponse> receiveMessage(
            @DestinationVariable final Long roomId,
            @RequestBody final ChatMessageRequest chatMessageRequest
    ) {
        // 메시지를 해당 채팅방 구독자들에게 전송
        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .nickname(chatMessageRequest.getNickname())
                .message(chatMessageRequest.getMessage())
                .roomId(roomId)
                .date(chatMessageRequest.getDate())
                .build();
        return ResponseEntity.ok(chatMessageResponse);
    }

    @GetMapping("/chat/rooms")
    public ResponseEntity<ChatRoomsResponse> getChatRooms() {
        return ResponseEntity.ok(chatService.getChatRooms());
    }
}
