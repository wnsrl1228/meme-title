package com.memetitle.member.service;

import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.domain.TopTitle;
import com.memetitle.meme.repository.MemeRepository;
import com.memetitle.meme.repository.TitleRepository;
import com.memetitle.meme.repository.TopTitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberSchedulerService {

    private final TitleRepository titleRepository;
    private final MemeRepository memeRepository;
    private final TopTitleRepository topTitleRepository;

    @Scheduled(cron = "0 0 0 * * *") // 매일 0시에
    public void updateScoreByTitleLikeCount() {

        // 종료일이 오늘 날찌인 meme을 가져옴
        List<Meme> endedMemes = findEndedMemes();

        // 없을 경우 종료
        if (endedMemes.isEmpty()) return;

        // 있을 경우 해당 meme의 title을 likeCount가 많은 순서로 5개만 가져옴
        for (Meme meme : endedMemes) {

            // meme 상태를 ENDED로 변경
            meme.updateStatusToEnded();

            // 해당 meme의 title을 좋아요 개수가 많은 순서로 모두 가져옴
            List<Title> titles = titleRepository.findByMemeIdOrderByLikeCountDesc(meme.getId());
            if (titles.isEmpty()) continue;

            // 상위 5개의 title을 작성한 member에게 점수 부여
            // - 동수일 경우 전부 점수 부여
            calculateTitleScores(titles);

            // 상위 3개의 제목은 TopTitle에 저장
            // - 동수 무시하고 3개만 저장
            saveTopTitles(meme.getId(), titles);
        }
    }

    private List<Meme> findEndedMemes() {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0));
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,0,0));
        return memeRepository.findAllByEndDateBetween(startTime, endTime);
    }

    private void calculateTitleScores(List<Title> titles) {
        int beforeLikeCount = titles.get(0).getLikeCount();
        int point = 100;
        for (Title title : titles) {
            // 좋아요 수가 0일 경우 점수 X
            if (title.getLikeCount() == 0) break;

            if (beforeLikeCount != title.getLikeCount()) {
                if (point == 20) break;
                point -= 20;
            }
            title.getMember().plusScore(point);
            beforeLikeCount = title.getLikeCount();
        }
    }

    private void saveTopTitles(Long memeId, List<Title> titles) {
        int ranking = 1;
        int beforeLikeCount = titles.get(0).getLikeCount();
        for (int i = 0; i < Math.min(3, titles.size()); i++) {
            Title title = titles.get(i);
            if (beforeLikeCount == title.getLikeCount()) {
                topTitleRepository.save(TopTitle.of(memeId, title, ranking));
            } else {
                ranking++;
                beforeLikeCount = title.getLikeCount();
                topTitleRepository.save(TopTitle.of(memeId, title, ranking));
            }
        }
    }

}
