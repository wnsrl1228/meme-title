package com.memetitle.meme.service;

import com.memetitle.global.exception.InvalidException;
import com.memetitle.meme.domain.TopTitle;
import com.memetitle.meme.dto.response.TopTitlesResponse;
import com.memetitle.meme.repository.MemeRepository;
import com.memetitle.meme.repository.TopTitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.memetitle.global.exception.ErrorCode.NOT_FOUND_MEME_ID;

@Service
@Transactional
@RequiredArgsConstructor
public class TopTitleService {

    private final TopTitleRepository topTitleRepository;
    private final MemeRepository memeRepository;

    public TopTitlesResponse getTopTitlesByMemeId(Long memeId) {
        if (!memeRepository.existsById(memeId)) {
            throw new InvalidException(NOT_FOUND_MEME_ID);
        }
        List<TopTitle> topTitles = topTitleRepository.findByMemeIdOrderByRank(memeId);
        return TopTitlesResponse.ofTopTitles(topTitles);
    }
}
