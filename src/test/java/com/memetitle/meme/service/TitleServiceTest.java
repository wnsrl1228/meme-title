package com.memetitle.meme.service;

import com.memetitle.global.exception.ErrorCode;
import com.memetitle.global.exception.InvalidException;
import com.memetitle.member.domain.Member;
import com.memetitle.member.repository.MemberRepository;
import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.domain.TopTitle;
import com.memetitle.meme.dto.TitleElement;
import com.memetitle.meme.dto.request.TitleCreateRequest;
import com.memetitle.meme.dto.response.TitleDetailResponse;
import com.memetitle.meme.dto.response.TitlesResponse;
import com.memetitle.meme.repository.MemeRepository;
import com.memetitle.meme.repository.TitleRepository;
import com.memetitle.meme.repository.TopTitleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.data.domain.Sort.Direction.DESC;

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
    @Autowired
    private TopTitleRepository topTitleRepository;

    private Member initMember;
    private Meme initMeme;
    private Title initTitle;

    @BeforeEach
    void init() {
        initMeme = memeRepository.save(new Meme("test.jpg", IMG_URL, LocalDateTime.now(), LocalDateTime.now()));
        initMember = memberRepository.save(new Member(SAMPLE_SNSTOKENID, SAMPLE_EMAIL, SAMPLE_NICKNAME));
        initTitle = titleRepository.save(new Title(initMeme, initMember, "안녕!"));
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
    @DisplayName("밈 제목 생성 시 이미 3개 이상의 제목을 작성했을 시 예외가 발생한다.")
    void saveTitle_MAX_NUMBER_OF_TITLES_REACHED() {
        // given
        titleRepository.save(new Title(initMeme, initMember, "안녕!"));
        titleRepository.save(new Title(initMeme, initMember, "안녕!"));
        titleRepository.save(new Title(initMeme, initMember, "안녕!"));

        TitleCreateRequest titleCreateRequest = new TitleCreateRequest(SAMPLE_TITLE);

        // then & when
        assertThatThrownBy(() -> titleService.saveTitle(initMember.getId(), initMeme.getId(), titleCreateRequest))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.MAX_NUMBER_OF_TITLES_REACHED.getMessage());
    }

    @Test
    @DisplayName("밈 제목 페이징 목록 불러오기를 성공한다.")
    void getPageableTitlesByMemeId_success() throws InterruptedException {
        // given
        for (int id=2;id<=4;id++) {
            Thread.sleep(1);
            titleRepository.save(new Title(initMeme, initMember, "안녕!"));
        }

        // when
        TitlesResponse titlesResponse = titleService.getPageableTitlesByMemeId(initMeme.getId(), PageRequest.of(0, 3, DESC, "createdAt"));
        List<TitleElement> titles = titlesResponse.getTitles();

        // then
        assertThat(titles.size()).isEqualTo(3);
        assertThat(titlesResponse.getIsLast()).isEqualTo(false);
        assertThat(titles.get(0).getId()).isEqualTo(4);
    }

    @Test
    @DisplayName("밈 제목 상세 정보 불러오기를 성공한다.")
    void getTitleDetail_success() {
        // when
        TitleDetailResponse titleDetailResponse = titleService.getTitleById(null, initTitle.getId());

        // then
        assertThat(titleDetailResponse.getId()).isEqualTo(initTitle.getId());
        assertThat(titleDetailResponse.getTitle()).isEqualTo(initTitle.getTitle());
        assertThat(titleDetailResponse.getMemeId()).isEqualTo(initTitle.getMemeId());
        assertThat(titleDetailResponse.getMember().getId()).isEqualTo(initTitle.getMember().getId());
        assertThat(titleDetailResponse.getCreatedAt()).isEqualTo(initTitle.getCreatedAt());
    }

    @Test
    @DisplayName("밈 제목 삭제를 성공한다.")
    void deleteTitle_success() {
        // when
        titleService.deleteTitle(initMember.getId(), initTitle.getId());

        // then
        assertThat(titleRepository.existsById(initTitle.getId())).isEqualTo(false);
    }

    @Test
    @DisplayName("밈 제목에 권한이 없는 사용자가 삭제할 경우 예외가 발생한다.")
    void deleteTitle_TITLE_ACCESS_DENIED() {
        // given
        Member newMember = memberRepository.save(new Member("111", "111", "111"));

        // when & then
        assertThatThrownBy(() -> titleService.deleteTitle(newMember.getId(), initTitle.getId()))
                 .isInstanceOf(InvalidException.class)
                         .hasMessage(ErrorCode.TITLE_ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("Top title을 삭제할 경우 예외가 발생한다.")
    void deleteTitle_TOP_TITLE_CANNOT_BE_DELETED() {
        // given

        topTitleRepository.save(TopTitle.of(initMeme.getId(), initTitle, 1));

        // when & then
        assertThatThrownBy(() -> titleService.deleteTitle(initMember.getId(), initTitle.getId()))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.TOP_TITLE_CANNOT_BE_DELETED.getMessage());
    }
}