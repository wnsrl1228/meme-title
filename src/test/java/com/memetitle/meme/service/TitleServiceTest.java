package com.memetitle.meme.service;

import com.memetitle.global.exception.ErrorCode;
import com.memetitle.global.exception.InvalidException;
import com.memetitle.mebmer.domain.Member;
import com.memetitle.mebmer.repository.MemberRepository;
import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.dto.TitleElement;
import com.memetitle.meme.dto.request.TitleCreateRequest;
import com.memetitle.meme.dto.response.TitlesResponse;
import com.memetitle.meme.repository.MemeRepository;
import com.memetitle.meme.repository.TitleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Sql({"/h2-truncate.sql"})
class TitleServiceTest {

    private static final String IMG_URL = "image-url";
    private static final String SAMPLE_SNSTOKENID = "123";
    private static final String SAMPLE_EMAIL = "hello123@naver.com";
    private static final String SAMPLE_NICKNAME = "hello";
    private static final String SAMPLE_TITLE = "제목입니다.";
    private static final Long INVALID_ID = 9999L;
    @Autowired
    private TitleService titleService;

    @Autowired
    private MemeRepository memeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TitleRepository titleRepository;

    private Member initMember;
    private Meme initMeme;
    private Title initTitle;

    @BeforeEach
    void init() {
        initMeme = memeRepository.save(new Meme(IMG_URL, LocalDate.now(), LocalDate.now()));
        initMember = memberRepository.save(new Member(SAMPLE_SNSTOKENID, SAMPLE_EMAIL, SAMPLE_NICKNAME));
        initTitle = titleRepository.save(new Title(initMeme.getId(), initMember, "안녕!"));
    }

    @Test
    @DisplayName("밈 제목 생성에 성공한다.")
    void saveTitle_success() {
        // given
        TitleCreateRequest titleCreateRequest = new TitleCreateRequest(SAMPLE_TITLE);

        // when
        Long titleId = titleService.saveTitle(initMember.getId(), initMeme.getId(), titleCreateRequest);

        // then
        Title title = titleRepository.findById(titleId).get();

        assertThat(titleId).isEqualTo(2L);
        assertThat(title.getTitle()).isEqualTo(SAMPLE_TITLE);
        assertThat(title.getMemeId()).isEqualTo(initMeme.getId());
        assertThat(title.getMember()).isEqualTo(initMember);
    }

    @Test
    @DisplayName("밈 제목 생성 시 멤버 id가 유효하지 않을 경우 예외가 발생한다.")
    void saveTitle_NOT_FOUND_MEMBER_ID() {
        // given
        TitleCreateRequest titleCreateRequest = new TitleCreateRequest(SAMPLE_TITLE);

        // then & when
        assertThatThrownBy(() -> titleService.saveTitle(INVALID_ID, initMeme.getId(), titleCreateRequest))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEMBER_ID.getMessage());
    }

    @Test
    @DisplayName("밈 제목 생성 시 밈 id가 유효하지 않을 경우 예외가 발생한다.")
    void saveTitle_NOT_FOUND_MEME_ID() {
        // given
        TitleCreateRequest titleCreateRequest = new TitleCreateRequest(SAMPLE_TITLE);

        // then & when
        assertThatThrownBy(() -> titleService.saveTitle(initMember.getId(), INVALID_ID, titleCreateRequest))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.NOT_FOUND_MEME_ID.getMessage());
    }

    @Test
    @DisplayName("밈 제목 목록 불러오기를 성공한다.")
    void getTitlesByMemeId_success() {
        // when
        TitlesResponse titlesResponse = titleService.getTitlesByMemeId(initMeme.getId());
        TitleElement titleElement = titlesResponse.getTitles().get(0);

        // then
        assertThat(titlesResponse.getTitles().size()).isEqualTo(1);
        assertThat(titleElement.getId()).isEqualTo(initTitle.getId());
        assertThat(titleElement.getTitle()).isEqualTo(initTitle.getTitle());
        assertThat(titleElement.getMemeId()).isEqualTo(initTitle.getMemeId());
        assertThat(titleElement.getMember().getId()).isEqualTo(initTitle.getMember().getId());
        assertThat(titleElement.getCreatedAt()).isEqualTo(initTitle.getCreatedAt());
    }
}