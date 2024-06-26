package com.memetitle.meme.service;

import com.memetitle.global.exception.InvalidException;
import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.dto.MemeElement;
import com.memetitle.meme.dto.response.MemesResponse;
import com.memetitle.meme.repository.MemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.memetitle.global.exception.ErrorCode.NOT_FOUND_MEME_ID;


@Service
@Transactional
@RequiredArgsConstructor
public class MemeService {

    private final MemeRepository memeRepository;

    public Long saveMeme(final String imgUrl, final String imgOriginalName) {
        final LocalDateTime startDate = LocalDateTime.now();
        final LocalDateTime endDate = startDate.plusDays(7);
        final Meme meme = new Meme(
                imgOriginalName,
                imgUrl,
                startDate,
                endDate
        );

        return memeRepository.save(meme).getId();
    }

    @Transactional(readOnly = true)
    public MemesResponse getPageableMemes(final Pageable pageable) {
        Slice<Meme> memes = memeRepository.findAll(pageable);
        return MemesResponse.ofMemes(memes);
    }


    public MemeElement getMemeByMemeId(Long memeId) {
        Meme meme = memeRepository.findById(memeId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_MEME_ID));
        return MemeElement.of(meme);
    }
}
