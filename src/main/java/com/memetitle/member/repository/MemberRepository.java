package com.memetitle.member.repository;

import com.memetitle.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySnsTokenId(String snsTokenId);

    boolean existsByNickname(String nickname);
}
