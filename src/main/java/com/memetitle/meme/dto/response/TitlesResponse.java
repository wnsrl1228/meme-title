package com.memetitle.meme.dto.response;

import com.memetitle.meme.domain.Title;
import com.memetitle.meme.dto.TitleElement;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class TitlesResponse {

    private List<TitleElement> titles;

    public static TitlesResponse ofTitles(List<Title> titles) {
        final List<TitleElement> titleElements = titles.stream()
                .map(TitleElement::of)
                .collect(Collectors.toList());

        return TitlesResponse.builder()
                .titles(titleElements)
                .build();
    }
}
