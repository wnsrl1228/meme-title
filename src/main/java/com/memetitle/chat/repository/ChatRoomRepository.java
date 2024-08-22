package com.memetitle.chat.repository;

import com.memetitle.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Modifying
    @Query(value = "UPDATE ChatRoom cr set cr.memberCount = cr.memberCount + 1 where cr.id = :id")
    void increaseMemberCount(Long id);

    @Modifying
    @Query(value = "UPDATE ChatRoom cr set cr.memberCount = cr.memberCount - 1 where cr.id = :id")
    void decreaseMemberCount(Long id);
}
