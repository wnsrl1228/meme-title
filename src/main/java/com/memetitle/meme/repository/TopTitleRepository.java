package com.memetitle.meme.repository;

import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.domain.TopTitle;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopTitleRepository extends JpaRepository<TopTitle, Long> {

    @EntityGraph(attributePaths = {"member"})
    List<TopTitle> findByMemeIdOrderByRanking(Long memeId);

    boolean existsByTitleId(Long titleId);

    @EntityGraph(attributePaths = {"member"})
    Optional<TopTitle> findFirstByMemeIdOrderByRanking(Long memeId);
}
