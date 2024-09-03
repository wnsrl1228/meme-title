package com.memetitle.chat.config;

import com.memetitle.chat.domain.ChatRoom;
import com.memetitle.chat.repository.ChatRoomRepository;
import com.memetitle.global.exception.InvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static com.memetitle.global.exception.ErrorCode.NOT_FOUND_CHATROOM_ID;

@Component
@RequiredArgsConstructor
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String path = request.getURI().getPath();
        String roomId = path.split("/")[2];
        ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(roomId))
                .orElseThrow(() -> new InvalidException(NOT_FOUND_CHATROOM_ID));

        if (chatRoom.getMemberCount() == chatRoom.getMaxCapacity()) {
            return false;
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {}
}
