package com.memetitle.chat.config;

import com.memetitle.chat.repository.ChatRoomStorage;
import com.memetitle.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

/**
 * 채팅방 인원 관리용 인터셉터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {

    private final ChatService chatService;
    private final ChatRoomStorage chatRoomStorage;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);

        if (stompHeaderAccessor != null) {
            StompCommand command = stompHeaderAccessor.getCommand();

            switch (command) {
                case SUBSCRIBE -> {
                    String simpDestination = (String) message.getHeaders().get("simpDestination");
                    String roomId = simpDestination.split("/")[3];
                    chatService.increaseMemberCount(Long.parseLong(roomId));

                    String sessionId = (String) message.getHeaders().get("simpSessionId");
                    chatRoomStorage.storage(sessionId, Long.parseLong(roomId));

                    log.info("websocket subscribe");
                }
                case DISCONNECT -> {
                    String sessionId = (String) message.getHeaders().get("simpSessionId");
                    Long roomId = chatRoomStorage.getRoomId(sessionId);
                    chatService.decreaseMemberCount(roomId);
                    chatRoomStorage.delete(sessionId);
                    log.info("websocket disconnect");
                }
            }
        }
        return message;
    }
}
