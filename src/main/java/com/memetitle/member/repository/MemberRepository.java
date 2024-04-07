package com.memetitle.member.repository;

import com.memetitle.member.domain.Member;
import com.memetitle.member.dto.RankDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySnsTokenId(String snsTokenId);

    boolean existsByNickname(String nickname);

    @Query(value = "SELECT dense_rank() over (order by m.score desc) as ranking, m.id as memberId, m.nickname as nickname, m.img_url as imgUrl, m.score as score" +
            " from Member m " ,
            countQuery = "select count(*) from Member",
            nativeQuery = true
    )
    Page<RankDto> findMembersRanking(Pageable pageable);
}
