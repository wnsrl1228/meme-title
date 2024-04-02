package com.memetitle.meme.dto.response;

import com.memetitle.mebmer.dto.response.MemberResponse;
import com.memetitle.meme.domain.Title;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TitleDetailResponse {

    private Long id;
    private Long memeId;
    private String title;
    private MemberResponse member;
    private LocalDateTime createdAt;

    public static TitleDetailResponse of(Title title) {
        return TitleDetailResponse.builder()
                .id(title.getId())
                .memeId(title.getMemeId())
                .title(title.getTitle())
                .member(MemberResponse.of(title.getMember()))
                .createdAt(title.getCreatedAt())
                .build();

    }
}