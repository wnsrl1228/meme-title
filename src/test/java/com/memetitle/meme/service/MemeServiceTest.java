package com.memetitle.meme.service;

import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.dto.response.MemesResponse;
import com.memetitle.meme.repository.MemeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Sql({"/h2-truncate.sql"})
class MemeServiceTest {

    private static final String IMG_URL = "image-url";
    private static final String IMG_ORIGINAL_NAME = "test.jpg";
    @Autowired
    private MemeService memeService;
    @Autowired
    private MemeRepository memeRepository;

    @Test
    @DisplayName("밈 생성에 성공한다.")
    void saveMeme() {
        // when
        Long memeId = memeService.saveMeme(IMG_URL, IMG_ORIGINAL_NAME);

        // then
        Meme meme = memeRepository.findById(memeId).get();

        assertThat(meme.getId()).isEqualTo(memeId);
        assertThat(meme.getImgUrl()).isEqualTo(IMG_URL);
        assertThat(meme.getImgOriginalName()).isEqualTo(IMG_ORIGINAL_NAME);
        assertThat(meme.getStartDate()).isEqualTo(LocalDate.now());
        assertThat(meme.getEndDate()).isEqualTo(LocalDate.now().plusDays(7));
    }

    @Test
    @DisplayName("모든 밈 조회에 성공한다.")
    void getMemeAll() {
        // then
        memeService.saveMeme(IMG_URL, IMG_ORIGINAL_NAME);

        // when
        MemesResponse memesResponse = memeService.getMemeAll();

        // then
        assertThat(memesResponse.getMemes().size()).isEqualTo(1);
    }
}