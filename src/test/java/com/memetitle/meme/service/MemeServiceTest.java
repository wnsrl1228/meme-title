package com.memetitle.meme.service;

import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.dto.MemeElement;
import com.memetitle.meme.dto.response.MemesResponse;
import com.memetitle.meme.repository.MemeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.Direction.DESC;

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
    void saveMeme_success() {
        // when
        Long memeId = memeService.saveMeme(IMG_URL, IMG_ORIGINAL_NAME);

        // then
        Meme meme = memeRepository.findById(memeId).get();

        assertThat(meme.getId()).isEqualTo(memeId);
        assertThat(meme.getImgUrl()).isEqualTo(IMG_URL);
        assertThat(meme.getImgOriginalName()).isEqualTo(IMG_ORIGINAL_NAME);
        assertThat(meme.getStartDate().toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(meme.getEndDate().toLocalDate()).isEqualTo(LocalDate.now().plusDays(7));
    }

    @Test
    @DisplayName("페이징 처리된 밈 조회에 성공한다.")
    void getPageableMemes_success() throws InterruptedException {
        // given
        for (int i=1;i<=21;i++) {
            Thread.sleep(1);
            memeService.saveMeme(IMG_URL, IMG_ORIGINAL_NAME);
        }

        // when
        MemesResponse memesResponse = memeService.getPageableMemes(PageRequest.of(0, 20, DESC, "startDate"));
        List<MemeElement> memes = memesResponse.getMemes();

        // then
        assertThat(memesResponse.getIsLast()).isEqualTo(false);
        assertThat(memes.size()).isEqualTo(20);
        assertThat(memes.get(0).getId()).isEqualTo(21);
    }

    @Test
    @DisplayName("밈 조회에 성공한다.")
    void getMemeByMemeId_success(){
        // given
        Long memeId = memeService.saveMeme(IMG_URL, IMG_ORIGINAL_NAME);

        // when
        MemeElement memeElement = memeService.getMemeByMemeId(memeId);

        // then
        assertThat(memeElement.getId()).isEqualTo(memeId);
    }
}