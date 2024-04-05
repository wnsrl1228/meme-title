package com.memetitle.meme.service;

import com.memetitle.global.exception.ErrorCode;
import com.memetitle.global.exception.InvalidException;
import com.memetitle.member.domain.Member;
import com.memetitle.member.repository.MemberRepository;
import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.domain.TitleLike;
import com.memetitle.meme.repository.MemeRepository;
import com.memetitle.meme.repository.TitleLikeRepository;
import com.memetitle.meme.repository.TitleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Sql({"/h2-truncate.sql"})
class TitleLikeServiceTest {

    private static final String IMG_URL = "image-url";
    private static final String SAMPLE_SNSTOKENID = "123";
    private static final String SAMPLE_EMAIL = "hello123@naver.com";
    private static final String SAMPLE_NICKNAME = "hello";
    private static final String SAMPLE_TITLE = "제목입니다.";
    private static final Long INVALID_ID = 9999L;

    @Autowired
    private TitleLikeService titleLikeService;
    @Autowired
    private TitleLikeRepository titleLikeRepository;
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
        initMeme = memeRepository.save(new Meme("test.jpg", IMG_URL, LocalDateTime.now(), LocalDateTime.now()));
        initMember = memberRepository.save(new Member(SAMPLE_SNSTOKENID, SAMPLE_EMAIL, SAMPLE_NICKNAME));
        initTitle = titleRepository.save(new Title(initMeme.getId(), initMember, "안녕!"));
    }

    @Test
    @DisplayName("제목 좋아요에 성공한다.")
    void saveLike_success() {
        // given
        Member newMember = memberRepository.save(new Member("123123", "other@123", "otherMember"));

        // when
        Long titleLikeId = titleLikeService.saveLike(newMember.getId(), initTitle.getId());

        // then
        TitleLike titleLike = titleLikeRepository.findById(titleLikeId).get();
        assertThat(titleLike.getTitle().getId()).isEqualTo(initTitle.getId());
        assertThat(titleLike.getMember().getId()).isEqualTo(newMember.getId());
    }

    @Test
    @DisplayName("본인 제목에 좋아요할 경우 예외가 발생한다.")
    void saveLike_SELF_TITLE_LIKE_DISALLOWED() {
        // when & then
        assertThatThrownBy(() -> titleLikeService.saveLike(initMember.getId(), initTitle.getId()))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.SELF_TITLE_LIKE_DISALLOWED.getMessage());
    }

    @Test
    @DisplayName("이미 좋아요한 제목에 좋아요를 할 경우 예외가 발생한다.")
    void saveLike_DUPLICATE_TITLE_LIKE() {
        // given
        Member newMember = memberRepository.save(new Member("123123", "other@123", "otherMember"));
        titleLikeRepository.save(new TitleLike(newMember, initTitle));

        // when & then
        assertThatThrownBy(() -> titleLikeService.saveLike(newMember.getId(), initTitle.getId()))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.DUPLICATE_TITLE_LIKE.getMessage());
    }

    @Test
    @DisplayName("좋아요 취소에 성공한다.")
    void deleteLike_success() {
        // given
        Member newMember = memberRepository.save(new Member("123123", "other@123", "otherMember"));
        titleLikeRepository.save(new TitleLike(newMember, initTitle));

        // when
        titleLikeService.deleteLike(newMember.getId(), initTitle.getId());

        // then
        boolean result = titleLikeRepository.existsByMemberAndTitle(newMember, initTitle);
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("본인 글에 좋아요 취소할 경우 예외가 발생한다.")
    void deleteLike_SELF_TITLE_LIKE_DISALLOWED() {
        // when & then
        assertThatThrownBy(() -> titleLikeService.deleteLike(initMember.getId(), initTitle.getId()))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.SELF_TITLE_LIKE_DISALLOWED.getMessage());
    }

    @Test
    @DisplayName("좋아요한 적 없는 제목을 삭제할 경우 예외가 발생한다.")
    void deleteLike_NOT_FOUND_TITLE_LIKE() {
        // given
        Member newMember = memberRepository.save(new Member("123123", "other@123", "otherMember"));

        // when & then
        assertThatThrownBy(() -> titleLikeService.deleteLike(newMember.getId(), initTitle.getId()))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.NOT_FOUND_TITLE_LIKE.getMessage());
    }
}