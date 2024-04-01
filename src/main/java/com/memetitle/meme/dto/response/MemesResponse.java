package com.memetitle.meme.dto.response;

import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.dto.MemeElement;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MemesResponse {
    private List<MemeElement> memes;

    public static MemesResponse ofMemes(List<Meme> memes) {
        final List<MemeElement> memeElements = memes.stream()
                .map(MemeElement::of)
                .collect(Collectors.toList());

        return MemesResponse.builder()
                .memes(memeElements)
                .build();
    }
}
