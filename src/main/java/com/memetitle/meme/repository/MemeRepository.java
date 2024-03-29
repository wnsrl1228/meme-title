package com.memetitle.meme.repository;

import com.memetitle.meme.domain.Meme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemeRepository extends JpaRepository<Meme, Long> {
}
