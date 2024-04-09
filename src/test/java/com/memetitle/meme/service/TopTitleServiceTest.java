package com.memetitle.meme.service;

import com.memetitle.member.domain.Member;
import com.memetitle.member.repository.MemberRepository;
import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.domain.TopTitle;
import com.memetitle.meme.dto.TitleElement;
import com.memetitle.meme.dto.response.TopTitlesResponse;
import com.memetitle.meme.repository.MemeRepository;
import com.memetitle.meme.repository.TitleRepository;
import com.memetitle.meme.repository.TopTitleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@Sql({"/h2-truncate.sql"})
class TopTitleServiceTest {

    private static final String IMG_URL = "image-url";
    private static final String SAMPLE_SNSTOKENID = "123";
    private static final String SAMPLE_EMAIL = "hello123@naver.com";
    private static final String SAMPLE_NICKNAME = "hello";
    private static final String SAMPLE_TITLE = "제목입니다.";
    private static final Long INVALID_ID = 9999L;

    @Autowired
    private TopTitleService topTitleService;
    @Autowired
    private MemeRepository memeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TitleRepository titleRepository;
    @Autowired
    private TopTitleRepository topTitleRepository;
    private Member initMember;
    private Meme initMeme;
    private Title initTitle;

    @BeforeEach
    void init() {
        initMeme = memeRepository.save(new Meme("test.jpg", IMG_URL, LocalDateTime.now(), LocalDateTime.now()));
        initMember = memberRepository.save(new Member(SAMPLE_SNSTOKENID, SAMPLE_EMAIL, SAMPLE_NICKNAME));
        initTitle = titleRepository.save(new Title(initMeme.getId(), initMember, "안녕!"));
    }

    @Test
    @DisplayName("해당 meme에 대한 Toptitle을 불러오기를 성공한다.")
    void getTopTitlesByMemeId_success() {
        // given
        Title title2 = titleRepository.save(new Title(initMeme.getId(), initMember, "안녕!"));
        Title title3 = titleRepository.save(new Title(initMeme.getId(), initMember, "안녕!"));

        topTitleRepository.save(TopTitle.of(initMeme.getId(), initTitle, 1));
        topTitleRepository.save(TopTitle.of(initMeme.getId(), title2, 2));
        topTitleRepository.save(TopTitle.of(initMeme.getId(), title3, 3));

        // when
        TopTitlesResponse topTitlesResponse = topTitleService.getTopTitlesByMemeId(initMeme.getId());
        List<TitleElement> titles = topTitlesResponse.getTitles();

        // then
        assertThat(titles.get(0).getId()).isEqualTo(initTitle.getId());
        assertThat(titles.get(1).getId()).isEqualTo(title2.getId());
        assertThat(titles.get(2).getId()).isEqualTo(title3.getId());
    }
}