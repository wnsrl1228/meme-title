package com.memetitle.meme.repository;

import com.memetitle.meme.domain.Title;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TitleRepository extends JpaRepository<Title, Long> {

    @EntityGraph(attributePaths = {"member"})
    Slice<Title> findByMemeId(Long memeId, Pageable pageable);

    @EntityGraph(attributePaths = {"member"})
    Slice<Title> findByMemberId(Long memberId, Pageable pageable);
}
