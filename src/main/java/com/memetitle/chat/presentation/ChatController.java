package com.memetitle.chat.presentation;

import com.memetitle.chat.dto.request.ChatMessageRequest;
import com.memetitle.chat.dto.response.ChatMessageResponse;
import com.memetitle.chat.dto.response.ChatRoomsResponse;
import com.memetitle.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    /**
     * 클라이언트에서 메세지 요청시 : /pub/chat/message
     * 채팅방 사람들에게 메세지 전달 : /sub/chat/messages
     */
    @MessageMapping("/chat/message")
    @SendTo("/sub/chat/messages")
    public ResponseEntity<ChatMessageResponse> receiveMessage(@RequestBody ChatMessageRequest chatMessageRequest) {
        // 메시지를 해당 채팅방 구독자들에게 전송
        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .nickname(chatMessageRequest.getNickname())
                .message(chatMessageRequest.getMessage())
                .build();
        return ResponseEntity.ok(chatMessageResponse);
    }

    @GetMapping("/chat/rooms")
    public ResponseEntity<ChatRoomsResponse> getChatRooms() {
        return ResponseEntity.ok(chatService.getChatRooms());
    }
}
