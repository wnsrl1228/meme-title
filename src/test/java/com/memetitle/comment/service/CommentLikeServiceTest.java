package com.memetitle.comment.service;

import com.memetitle.comment.domain.Comment;
import com.memetitle.comment.domain.CommentLike;
import com.memetitle.comment.repository.CommentLikeRepository;
import com.memetitle.comment.repository.CommentRepository;
import com.memetitle.global.exception.ErrorCode;
import com.memetitle.global.exception.InvalidException;
import com.memetitle.member.domain.Member;
import com.memetitle.member.repository.MemberRepository;
import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.repository.MemeRepository;
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

@SpringBootTest
@Transactional
@Sql({"/h2-truncate.sql"})
class CommentLikeServiceTest {

    private static final String IMG_URL = "image-url";
    private static final String SAMPLE_SNSTOKENID = "123";
    private static final String SAMPLE_EMAIL = "hello123@naver.com";
    private static final String SAMPLE_NICKNAME = "hello";
    private static final String SAMPLE_COMMENT = "댓글입니다.";
    private static final String SAMPLE_TITLE = "제목입니다.";
    private static final Long INVALID_ID = 9999L;

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentLikeService commentLikeService;
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private MemeRepository memeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TitleRepository titleRepository;

    private Member initMember;
    private Meme initMeme;
    private Title initTitle;
    private Comment initComment;

    @BeforeEach
    void init() {
        initMeme = memeRepository.save(new Meme("test.jpg", IMG_URL, LocalDateTime.now(), LocalDateTime.now()));
        initMember = memberRepository.save(new Member(SAMPLE_SNSTOKENID, SAMPLE_EMAIL, SAMPLE_NICKNAME));
        initTitle = titleRepository.save(new Title(initMeme, initMember, "안녕!"));
        initComment = commentRepository.save(new Comment(SAMPLE_COMMENT, initMember, initTitle));
    }

    @Test
    @DisplayName("댓글 좋아요에 성공한다.")
    void saveLike_success() {
        // given
        Member newMember = memberRepository.save(new Member("123123", "other@123", "otherMember"));

        // when
        Long commentLikeId = commentLikeService.saveLike(newMember.getId(), initComment.getId());

        // then
        CommentLike commentLike = commentLikeRepository.findById(commentLikeId).get();
        assertThat(commentLike.getComment().getId()).isEqualTo(initComment.getId());
        assertThat(commentLike.getMember().getId()).isEqualTo(newMember.getId());
    }

    @Test
    @DisplayName("본인 댓글에 좋아요할 경우 예외가 발생한다.")
    void saveLike_SELF_COMMENT_LIKE_DISALLOWED() {
        // when & then
        assertThatThrownBy(() -> commentLikeService.saveLike(initMember.getId(), initComment.getId()))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.SELF_COMMENT_LIKE_DISALLOWED.getMessage());
    }

    @Test
    @DisplayName("이미 좋아요한 댓글에 좋아요를 할 경우 예외가 발생한다.")
    void saveLike_DUPLICATE_COMMENT_LIKE() {
        // given
        Member newMember = memberRepository.save(new Member("123123", "other@123", "otherMember"));
        commentLikeRepository.save(new CommentLike(newMember, initComment));

        // when & then
        assertThatThrownBy(() -> commentLikeService.saveLike(newMember.getId(), initComment.getId()))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.DUPLICATE_COMMENT_LIKE.getMessage());
    }

    @Test
    @DisplayName("댓글 좋아요 취소에 성공한다.")
    void deleteLike_success() {
        // given
        Member newMember = memberRepository.save(new Member("123123", "other@123", "otherMember"));
        commentLikeRepository.save(new CommentLike(newMember, initComment));

        // when
        commentLikeService.deleteLike(newMember.getId(), initComment.getId());

        // then
        boolean result = commentLikeRepository.existsByMemberAndComment(newMember, initComment);
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("본인 댓글에 좋아요 취소할 경우 예외가 발생한다.")
    void deleteLike_SELF_COMMENT_LIKE_DISALLOWED() {
        // when & then
        assertThatThrownBy(() -> commentLikeService.deleteLike(initMember.getId(), initComment.getId()))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.SELF_COMMENT_LIKE_DISALLOWED.getMessage());
    }

    @Test
    @DisplayName("좋아요한 적 없는 댓글을 삭제할 경우 예외가 발생한다.")
    void deleteLike_NOT_FOUND_COMMENT_LIKE() {
        // given
        Member newMember = memberRepository.save(new Member("123123", "other@123", "otherMember"));

        // when & then
        assertThatThrownBy(() -> commentLikeService.deleteLike(newMember.getId(), initComment.getId()))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.NOT_FOUND_COMMENT_LIKE.getMessage());
    }
}