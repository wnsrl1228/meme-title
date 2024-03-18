package com.memetitle.mebmer.repository;

import com.memetitle.mebmer.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySnsTokenId(String snsTokenId);
}
