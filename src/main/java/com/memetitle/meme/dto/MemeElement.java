package com.memetitle.meme.dto;

import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.domain.MemeStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MemeElement {

    private Long id;
    private String imgOriginalName;
    private String imgUrl;
    private Boolean isInProgress;
    private LocalDate startDate;
    private LocalDate endDate;


    public static MemeElement of(Meme meme) {
        return MemeElement.builder()
                .id(meme.getId())
                .imgOriginalName(meme.getImgOriginalName())
                .imgUrl(meme.getImgUrl())
                .isInProgress(meme.isInProgress())
                .startDate(meme.getStartDate().toLocalDate())
                .endDate(meme.getEndDate().toLocalDate())
                .build();
    }
}