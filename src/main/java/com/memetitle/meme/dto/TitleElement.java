package com.memetitle.meme.dto;

import com.memetitle.member.dto.response.MemberResponse;
import com.memetitle.meme.domain.Title;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TitleElement {

    private Long id;
    private Long memeId;
    private String title;
    private MemberResponse member;
    private int likeCount;
    private LocalDateTime createdAt;

    public static TitleElement of(Title title) {
        return TitleElement.builder()
                .id(title.getId())
                .memeId(title.getMemeId())
                .title(title.getTitle())
                .member(MemberResponse.of(title.getMember()))
                .likeCount(title.getLikeCount())
                .createdAt(title.getCreatedAt())
                .build();
    }
}
