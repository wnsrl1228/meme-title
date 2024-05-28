package com.memetitle.meme.repository;

import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.domain.MemeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemeRepository extends JpaRepository<Meme, Long> {
    List<Meme> findAllByEndDateBetween(LocalDateTime start, LocalDateTime end);

    Optional<Meme> findFirstByStatusOrderByStartDateDesc(MemeStatus status);

    Optional<Meme> findFirstByOrderByIdDesc();
}
