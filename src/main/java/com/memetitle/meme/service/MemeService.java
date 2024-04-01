package com.memetitle.meme.service;

import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.dto.response.MemesResponse;
import com.memetitle.meme.repository.MemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class MemeService {

    private final MemeRepository memeRepository;

    public Long saveMeme(final String imgUrl, final String imgOriginalName) {
        final LocalDate startDate = LocalDate.now();
        final LocalDate endDate = startDate.plusDays(7);
        final Meme meme = new Meme(
                imgOriginalName,
                imgUrl,
                startDate,
                endDate
        );

        return memeRepository.save(meme).getId();
    }

    @Transactional(readOnly = true)
    public MemesResponse getMemeAll() {
        final List<Meme> memes = memeRepository.findAll();
        return MemesResponse.ofMemes(memes);
    }


}
