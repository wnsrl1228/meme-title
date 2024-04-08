package com.memetitle.meme.repository;

import com.memetitle.meme.domain.Meme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MemeRepository extends JpaRepository<Meme, Long> {
    List<Meme> findAllByEndDateBetween(LocalDateTime start, LocalDateTime end);
}
