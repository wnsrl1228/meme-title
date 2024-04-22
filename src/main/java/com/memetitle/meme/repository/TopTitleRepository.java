package com.memetitle.meme.repository;

import com.memetitle.meme.domain.TopTitle;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopTitleRepository extends JpaRepository<TopTitle, Long> {

    @EntityGraph(attributePaths = {"member"})
    List<TopTitle> findByMemeIdOrderByRanking(Long memeId);

    boolean existsByTitleId(Long titleId);
}
