package com.memetitle.member.service;

import com.memetitle.member.domain.Member;
import com.memetitle.member.repository.MemberRepository;
import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.dto.TitleElement;
import com.memetitle.meme.dto.response.TopTitlesResponse;
import com.memetitle.meme.repository.MemeRepository;
import com.memetitle.meme.repository.TitleRepository;
import com.memetitle.meme.service.TopTitleService;
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
class MemberSchedulerServiceTest {

    private static final String SAMPLE_TITLE = "제목입니다.";

    @Autowired
    private MemberSchedulerService memberSchedulerService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemeRepository memeRepository;
    @Autowired
    private TitleRepository titleRepository;
    @Autowired
    private TopTitleService topTitleService;

    private Meme initMeme;
    private Member member1;
    private Member member2;
    private Member member3;
    private Member member4;
    private Member member5;
    private Member member6;
    private Member member7;
    private Title title1;
    private Title title2;
    private Title title3;
    private Title title4;
    private Title title5;
    private Title title6;
    private Title title7;

    @BeforeEach
    void init() {
        initMeme = memeRepository.save(new Meme("test.jpg", "IMG_URL", LocalDateTime.now(), LocalDateTime.now()));
        member1 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_1", "SAMPLE_EMAIL_1", "SAMPLE_NICKNAME_1"));
        member2 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_2", "SAMPLE_EMAIL_2", "SAMPLE_NICKNAME_2"));
        member3 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_3", "SAMPLE_EMAIL_3", "SAMPLE_NICKNAME_3"));
        member4 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_4", "SAMPLE_EMAIL_4", "SAMPLE_NICKNAME_4"));
        member5 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_5", "SAMPLE_EMAIL_5", "SAMPLE_NICKNAME_5"));
        member6 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_6", "SAMPLE_EMAIL_6", "SAMPLE_NICKNAME_6"));
        member7 = memberRepository.save(new Member("SAMPLE_SNSTOKENID_7", "SAMPLE_EMAIL_7", "SAMPLE_NICKNAME_7"));

        title1 = titleRepository.save(new Title(initMeme, member1, SAMPLE_TITLE));
        title2 = titleRepository.save(new Title(initMeme, member2, SAMPLE_TITLE));
        title3 = titleRepository.save(new Title(initMeme, member3, SAMPLE_TITLE));
        title4 = titleRepository.save(new Title(initMeme, member4, SAMPLE_TITLE));
        title5 = titleRepository.save(new Title(initMeme, member5, SAMPLE_TITLE));
        title6 = titleRepository.save(new Title(initMeme, member6, SAMPLE_TITLE));
        title7 = titleRepository.save(new Title(initMeme, member7, SAMPLE_TITLE));
    }

    /**
     * 상황 : 좋아요 수가 많은 1~5등까지 점수 부여
     *       좋아요 수가 동일할 경우 동일 순위, 동일 점수 부여
     */
    @Test
    @DisplayName("meme의 제목 좋아요 수를 기준으로 유저 점수 업데이트를 성공한다.")
    void updateScoreByTitleLikeCount_1() {
        // give

        title1.increaseLike();title1.increaseLike();
        title2.increaseLike();title2.increaseLike();
        title3.increaseLike();title3.increaseLike();
        title4.increaseLike();title4.increaseLike();title4.increaseLike();title4.increaseLike();
        title5.increaseLike();title5.increaseLike();title5.increaseLike();title5.increaseLike();title5.increaseLike();
        title6.increaseLike();title6.increaseLike();title6.increaseLike();title6.increaseLike();title6.increaseLike();title6.increaseLike();
        title7.increaseLike();title7.increaseLike();title7.increaseLike();title7.increaseLike();title7.increaseLike();title7.increaseLike();title7.increaseLike();

        // when
        memberSchedulerService.updateScoreByTitleLikeCount();

        // then
        TopTitlesResponse topTitlesResponse = topTitleService.getTopTitlesByMemeId(initMeme.getId());
        List<TitleElement> titles = topTitlesResponse.getTitles();

        assertThat(titles.get(0).getId()).isEqualTo(title7.getId());
        assertThat(titles.get(1).getId()).isEqualTo(title6.getId());
        assertThat(titles.get(2).getId()).isEqualTo(title5.getId());

        assertThat(member1.getScore()).isEqualTo(20);
        assertThat(member2.getScore()).isEqualTo(20);
        assertThat(member3.getScore()).isEqualTo(20);
        assertThat(member4.getScore()).isEqualTo(40);
        assertThat(member5.getScore()).isEqualTo(60);
        assertThat(member6.getScore()).isEqualTo(80);
        assertThat(member7.getScore()).isEqualTo(100);
    }

    @Test
    @DisplayName("좋아요 수가 모두 동일한 경우 모두 100점을 얻는다")
    void updateScoreByTitleLikeCount_2() {
        // give
        title1.increaseLike();
        title2.increaseLike();
        title3.increaseLike();
        title4.increaseLike();
        title5.increaseLike();
        title6.increaseLike();
        title7.increaseLike();

        // when
        memberSchedulerService.updateScoreByTitleLikeCount();

        // then
        TopTitlesResponse topTitlesResponse = topTitleService.getTopTitlesByMemeId(initMeme.getId());
        List<TitleElement> titles = topTitlesResponse.getTitles();

        assertThat(titles.get(0).getId()).isEqualTo(title1.getId());
        assertThat(titles.get(1).getId()).isEqualTo(title2.getId());
        assertThat(titles.get(2).getId()).isEqualTo(title3.getId());

        assertThat(member1.getScore()).isEqualTo(100);
        assertThat(member2.getScore()).isEqualTo(100);
        assertThat(member3.getScore()).isEqualTo(100);
        assertThat(member4.getScore()).isEqualTo(100);
        assertThat(member5.getScore()).isEqualTo(100);
        assertThat(member6.getScore()).isEqualTo(100);
        assertThat(member7.getScore()).isEqualTo(100);
    }

    @Test
    @DisplayName("유저1이 쓴 제목이 1등 2등한 경우 180점을 얻고 유저2가 쓴 제목이 공동 3등을 한 경우 120점을 얻는다.")
    void updateScoreByTitleLikeCount_3() {
        // give
        Title title8 = titleRepository.save(new Title(initMeme, member1, SAMPLE_TITLE));
        title1.increaseLike();title1.increaseLike();title1.increaseLike();
        title8.increaseLike();title8.increaseLike();

        Title title9 = titleRepository.save(new Title(initMeme, member3, SAMPLE_TITLE));
        title3.increaseLike();
        title9.increaseLike();

        // when
        memberSchedulerService.updateScoreByTitleLikeCount();

        // then
        TopTitlesResponse topTitlesResponse = topTitleService.getTopTitlesByMemeId(initMeme.getId());
        List<TitleElement> titles = topTitlesResponse.getTitles();

        assertThat(titles.get(0).getId()).isEqualTo(title1.getId());
        assertThat(titles.get(1).getId()).isEqualTo(title8.getId());
        assertThat(titles.get(2).getId()).isEqualTo(title3.getId());

        assertThat(member1.getScore()).isEqualTo(180);
        assertThat(member3.getScore()).isEqualTo(120);
        assertThat(member2.getScore()).isEqualTo(0);
    }
}