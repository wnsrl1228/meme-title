package com.memetitle.meme.repository;

import com.memetitle.meme.domain.Title;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TitleRepository extends JpaRepository<Title, Long> {
    List<Title> findByMemeId(Long memeId);

    List<Title> findByMemberId(Long memberId);
}
