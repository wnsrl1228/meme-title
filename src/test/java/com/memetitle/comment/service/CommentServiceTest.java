package com.memetitle.comment.service;

import com.memetitle.comment.domain.Comment;
import com.memetitle.comment.dto.CommentElement;
import com.memetitle.comment.dto.request.CommentCreateRequest;
import com.memetitle.comment.dto.request.CommentModifyRequest;
import com.memetitle.comment.dto.response.CommentsResponse;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Sql({"/h2-truncate.sql"})
class CommentServiceTest {

    private static final String IMG_URL = "image-url";
    private static final String SAMPLE_SNSTOKENID = "123";
    private static final String SAMPLE_EMAIL = "hello123@naver.com";
    private static final String SAMPLE_NICKNAME = "hello";
    private static final String SAMPLE_TITLE = "제목입니다.";
    private static final String SAMPLE_COMMENT = "댓글입니다.";
    private static final Long INVALID_ID = 9999L;

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
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
        initTitle = titleRepository.save(new Title(initMeme.getId(), initMember, SAMPLE_TITLE));
        initComment = commentRepository.save(new Comment(SAMPLE_COMMENT, initMember, initTitle));
    }

    @Test
    @DisplayName("댓글 생성에 성공한다.")
    void saveComment_success() {
        // given
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest(SAMPLE_COMMENT);

        // when
        Long commentId = commentService.saveComment(initMember.getId(), initTitle.getId(), commentCreateRequest);

        // then
        Comment comment = commentRepository.findById(commentId).get();
        assertThat(comment.getId()).isEqualTo(commentId);
        assertThat(comment.getContent()).isEqualTo(SAMPLE_COMMENT);
        assertThat(comment.getTitle().getId()).isEqualTo(initTitle.getId());
        assertThat(comment.getMember().getId()).isEqualTo(initMember.getId());
    }

    @Test
    @DisplayName("해당 제목의 전체 댓글 목록 불러오기를 성공한다.")
    void getCommentsByTitleId_success() {
        // when
        CommentsResponse commentsResponse = commentService.getCommentsByTitleId(initTitle.getId());
        CommentElement commentElement = commentsResponse.getComments().get(0);

        // then
        assertThat(commentsResponse.getComments().size()).isEqualTo(1);
        assertThat(commentElement.getTitleId()).isEqualTo(initTitle.getId());
        assertThat(commentElement.getContent()).isEqualTo(initComment.getContent());
    }

    @Test
    @DisplayName("댓글 수정을 성공한다.")
    void updateComment_success() {
        // given
        CommentModifyRequest commentModifyRequest = new CommentModifyRequest("변경 내용");

        // when
        commentService.updateComment(initMember.getId(), initComment.getId(), commentModifyRequest);

        // then
        Comment comment = commentRepository.findById(initComment.getId()).get();

        assertThat(comment.getContent()).isEqualTo("변경 내용");
    }
    @Test
    @DisplayName("댓글 수정 권한이 없을 경우 예외가 발생한다.")
    void updateComment_TITLE_ACCESS_DENIED() {
        // given
        CommentModifyRequest commentModifyRequest = new CommentModifyRequest("변경 내용");
        Long newMemberId = memberRepository.save(new Member("111", "email", "nickname")).getId();

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(newMemberId, initComment.getId(), commentModifyRequest))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.COMMENT_ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("댓글 삭제를 성공한다.")
    void deleteComment_success() {
        // when
        commentService.deleteComment(initMember.getId(), initComment.getId());

        // then
        Optional<Comment> optionalComment = commentRepository.findById(initComment.getId());

        assertThat(optionalComment.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("댓글 삭제 권한이 없는 경우 예외가 발생한다.")
    void deleteComment_TITLE_ACCESS_DENIED() {
        // given
        Long newMemberId = memberRepository.save(new Member("111", "email", "nickname")).getId();

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(newMemberId, initComment.getId()))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.COMMENT_ACCESS_DENIED.getMessage());

    }
}