package com.memetitle.meme.repository;

import com.memetitle.member.domain.Member;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.domain.TitleLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TitleLikeRepository extends JpaRepository<TitleLike, Long> {
    boolean existsByMemberAndTitle(Member member, Title title);

    Optional<TitleLike> findByMemberAndTitle(Member member, Title title);

    boolean existsByMemberIdAndTitle(Long memberId, Title title);
}
