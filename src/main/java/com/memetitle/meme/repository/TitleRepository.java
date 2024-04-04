package com.memetitle.meme.repository;

import com.memetitle.meme.domain.Title;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TitleRepository extends JpaRepository<Title, Long> {
    Slice<Title> findByMemeId(Long memeId, Pageable pageable);

    Slice<Title> findByMemberId(Long memberId, Pageable pageable);
}
