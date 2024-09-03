package com.memetitle.chat.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@Slf4j
public class ChatRoomStorage {

    private final ConcurrentMap<String, Long> chatRoomMembers = new ConcurrentHashMap<>();

    public void storage(String sessionId, Long roomId) {
        chatRoomMembers.put(sessionId, roomId);
    }

    public Long getRoomId(String sessionId) {
        return chatRoomMembers.get(sessionId);
    }

    public void delete(String sessionId) {
        chatRoomMembers.remove(sessionId);
    }
}
