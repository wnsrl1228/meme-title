package com.memetitle.member.service;

import com.memetitle.comment.domain.Comment;
import com.memetitle.comment.dto.CommentElement;
import com.memetitle.comment.dto.response.CommentsResponse;
import com.memetitle.comment.repository.CommentRepository;
import com.memetitle.comment.service.CommentService;
import com.memetitle.global.exception.ErrorCode;
import com.memetitle.global.exception.InvalidException;
import com.memetitle.member.domain.Member;
import com.memetitle.member.dto.RankingElement;
import com.memetitle.member.dto.request.ProfileModifyRequest;
import com.memetitle.member.dto.response.OtherProfileResponse;
import com.memetitle.member.dto.response.ProfileResponse;
import com.memetitle.member.dto.response.RankingResponse;
import com.memetitle.member.repository.MemberRepository;
import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.dto.TitleElement;
import com.memetitle.meme.dto.response.TitlesResponse;
import com.memetitle.meme.repository.MemeRepository;
import com.memetitle.meme.repository.TitleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@SpringBootTest
@Transactional
@Sql({"/h2-truncate.sql"})
class MemberServiceTest {

    private static final String IMG_URL = "image-url";
    private static final String SAMPLE_SNSTOKENID = "123";
    private static final String SAMPLE_EMAIL = "hello123@naver.com";
    private static final String SAMPLE_NICKNAME = "hello";
    private static final String SAMPLE_TITLE = "제목입니다.";
    private static final String SAMPLE_COMMENT = "댓글입니다.";
    private static final Long INVALID_ID = 9999L;

    @Autowired
    private MemberService memberService;
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
    @DisplayName("프로필 정보 가져오기를 성공한다.")
    void getProfile_success() {
        // when
        ProfileResponse profile = memberService.getProfile(initMember.getId());

        // then
        assertThat(profile.getNickname()).isEqualTo(initMember.getNickname());
        assertThat(profile.getEmail()).isEqualTo(initMember.getEmail());
    }

    @Test
    @DisplayName("다른 유저의 프로필 정보 가져오기를 성공한다.")
    void getOtherProfile_success() {
        // when
        OtherProfileResponse otherProfile = memberService.getOtherProfile(initMember.getId());

        // then
        assertThat(otherProfile.getNickname()).isEqualTo(initMember.getNickname());
    }

    @Test
    @DisplayName("프로필 수정을 성공한다.")
    void updateProfile_success() {
        // given
        ProfileModifyRequest profileModifyRequest = new ProfileModifyRequest("new nickname", "new imgUrl");
        
        // when
        memberService.updateProfile(initMember.getId(), profileModifyRequest);

        // then
        Member member = memberRepository.findById(initMember.getId()).get();

        assertThat(member.getNickname()).isEqualTo(profileModifyRequest.getNickname());
        assertThat(member.getImgUrl()).isEqualTo(profileModifyRequest.getImgUrl());
    }

    @Test
    @DisplayName("프로필 수정 시 중복 닉네임인 경우 예외가 발생한다.")
    void updateProfile_DUPLICATE_NICKNAME() {
        // given
        String NEW_NICKNAME = "newNickname";
        memberRepository.save(new Member("new-token", "new-email", NEW_NICKNAME));
        ProfileModifyRequest profileModifyRequest = new ProfileModifyRequest(NEW_NICKNAME, "new imgUrl");

        // when & then
        assertThatThrownBy(() -> memberService.updateProfile(initMember.getId(), profileModifyRequest))
                .isInstanceOf(InvalidException.class)
                .hasMessage(ErrorCode.DUPLICATE_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("내가 쓴 제목 페이징 목록 불러오기를 성공한다.")
    void getPageableTitlesByMemberId_success() {
        // when
        TitlesResponse titlesResponse = memberService.getPageableTitlesByMemberId(initMember.getId(), PageRequest.of(0, 3, DESC, "createdAt"));
        TitleElement titleElement = titlesResponse.getTitles().get(0);

        // then
        assertThat(titlesResponse.getTitles().size()).isEqualTo(1);
        assertThat(titleElement.getTitle()).isEqualTo(initTitle.getTitle());
        assertThat(titleElement.getMember().getId()).isEqualTo(initTitle.getMember().getId());
    }

    @Test
    @DisplayName("내가 쓴 댓글 페이징 목록 불러오기를 성공한다.")
    void getPageableCommentsByMemberId_success() {
        // when
        CommentsResponse commentsResponse = memberService.getPageableCommentsByMemberId(initMember.getId(), PageRequest.of(0, 3, DESC, "createdAt"));
        CommentElement commentElement = commentsResponse.getComments().get(0);

        // then
        assertThat(commentsResponse.getComments().size()).isEqualTo(1);
        assertThat(commentElement.getContent()).isEqualTo(initComment.getContent());
        assertThat(commentElement.getTitleId()).isEqualTo(initComment.getTitle().getId());
        assertThat(commentElement.getMember().getId()).isEqualTo(initComment.getId());
    }

    @Test
    @DisplayName("유저 랭킹 페이징 목록 불러오기를 성공한다.")
    void getPageableMembersRanking_success() {
        // give
        Member member1 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_1", "SAMPLE_EMAIL_1", "SAMPLE_NICKNAME_1"));
        Member member2 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_2", "SAMPLE_EMAIL_2", "SAMPLE_NICKNAME_2"));
        Member member3 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_3", "SAMPLE_EMAIL_3", "SAMPLE_NICKNAME_3"));
        member1.updateScore(10);
        member2.updateScore(50);
        member3.updateScore(30);

        // when
        RankingResponse rankingResponse = memberService.getPageableMembersRanking(PageRequest.of(0, 2, DESC, "score"));
        RankingElement rankingElement = rankingResponse.getRanks().get(0);

        // then
        assertThat(rankingResponse.getRanks().size()).isEqualTo(2);
        assertThat(rankingElement.getRank()).isEqualTo(1);
        assertThat(rankingElement.getMember().getId()).isEqualTo(member2.getId());
        assertThat(rankingElement.getScore()).isEqualTo(50);
    }

    /**
     * 상황 : 좋아요 수가 많은 1~5등까지 점수 부여
     *       좋아요 수가 동일할 경우 동일 순위, 동일 점수 부여
     */
    @Test
    @DisplayName("meme의 제목 좋아요 수를 기준으로 유저 점수 업데이트를 성공한다.")
    void updateScoreByTitleLikeCount() {
        // give
        Member member1 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_1", "SAMPLE_EMAIL_1", "SAMPLE_NICKNAME_1"));
        Member member2 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_2", "SAMPLE_EMAIL_2", "SAMPLE_NICKNAME_2"));
        Member member3 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_3", "SAMPLE_EMAIL_3", "SAMPLE_NICKNAME_3"));
        Member member4 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_4", "SAMPLE_EMAIL_4", "SAMPLE_NICKNAME_4"));
        Member member5 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_5", "SAMPLE_EMAIL_5", "SAMPLE_NICKNAME_5"));
        Member member6 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_6", "SAMPLE_EMAIL_6", "SAMPLE_NICKNAME_6"));
        Member member7 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_7", "SAMPLE_EMAIL_7", "SAMPLE_NICKNAME_7"));

        Title title2 = titleRepository.save(new Title(initMeme.getId(), member2, SAMPLE_TITLE));
        Title title3 = titleRepository.save(new Title(initMeme.getId(), member3, SAMPLE_TITLE));
        Title title4 = titleRepository.save(new Title(initMeme.getId(), member4, SAMPLE_TITLE));
        Title title5 = titleRepository.save(new Title(initMeme.getId(), member5, SAMPLE_TITLE));
        Title title6 = titleRepository.save(new Title(initMeme.getId(), member6, SAMPLE_TITLE));
        Title title7 = titleRepository.save(new Title(initMeme.getId(), member7, SAMPLE_TITLE));

        title2.increaseLike();title2.increaseLike();
        title3.increaseLike();title3.increaseLike();
        title4.increaseLike();title4.increaseLike();title4.increaseLike();title4.increaseLike();
        title5.increaseLike();title5.increaseLike();title5.increaseLike();title5.increaseLike();title5.increaseLike();
        title6.increaseLike();title6.increaseLike();title6.increaseLike();title6.increaseLike();title6.increaseLike();title6.increaseLike();
        title7.increaseLike();title7.increaseLike();title7.increaseLike();title7.increaseLike();title7.increaseLike();title7.increaseLike();title7.increaseLike();

        memberService.updateScoreByTitleLikeCount();

        assertThat(member1.getScore()).isEqualTo(0);
        assertThat(member2.getScore()).isEqualTo(20);
        assertThat(member3.getScore()).isEqualTo(20);
        assertThat(member4.getScore()).isEqualTo(40);
        assertThat(member5.getScore()).isEqualTo(60);
        assertThat(member6.getScore()).isEqualTo(80);
        assertThat(member7.getScore()).isEqualTo(100);
    }
}