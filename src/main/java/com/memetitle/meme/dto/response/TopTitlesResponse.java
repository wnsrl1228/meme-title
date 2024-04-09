package com.memetitle.meme.dto.response;

import com.memetitle.meme.domain.TopTitle;
import com.memetitle.meme.dto.TitleElement;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class TopTitlesResponse {
    private List<TitleElement> titles;
    private Boolean isEmpty;

    public static TopTitlesResponse ofTopTitles(List<TopTitle> topTitles) {
        final List<TitleElement> titleElements = topTitles.stream()
                .map(TitleElement::of)
                .collect(Collectors.toList());

        return TopTitlesResponse.builder()
                .titles(titleElements)
                .isEmpty(topTitles.isEmpty())
                .build();
    }
}
