package com.memetitle.meme.service;

import com.memetitle.image.infrastructure.AwsS3Provider;
import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.repository.MemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemeSchedulerService {

    private final AwsS3Provider awsS3Provider;
    private final MemeRepository memeRepository;

    @Scheduled(cron = "0 1 0 */7 * *") // 0시 1분, 7일 주기
    public void generateMeme() {

        Long id = generateNextMemeId();

        String imgUrl = awsS3Provider.getUrlByPrefix(id.toString());

        // s3에 다음 밈에 대한 이미지가 없는 경우
        if (imgUrl == null) {
            log.info("s3에 밈에 대한 이미지가 없습니다.");
            return;
        }

        final Meme meme = new Meme(
                extractImgOriginalName(imgUrl),
                imgUrl,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );

        log.info("[generateMeme] success");
        memeRepository.save(meme);
    }

    private Long generateNextMemeId() {
        Optional<Meme> lastMeme = memeRepository.findFirstByOrderByIdDesc();
        return lastMeme.map(meme -> meme.getId() + 1).orElse(1L);
    }
    private String extractImgOriginalName(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
}
