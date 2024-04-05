package com.memetitle.meme.dto.response;

import com.memetitle.member.dto.response.MemberResponse;
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
    private int likeCount;
    private LocalDateTime createdAt;
    private Boolean isOwner;
    private Boolean isLiked;

    public static TitleDetailResponse of(Title title, Boolean isOwner, Boolean isLiked) {
        return TitleDetailResponse.builder()
                .id(title.getId())
                .memeId(title.getMemeId())
                .title(title.getTitle())
                .member(MemberResponse.of(title.getMember()))
                .likeCount(title.getLikeCount())
                .createdAt(title.getCreatedAt())
                .isOwner(isOwner)
                .isLiked(isLiked)
                .build();

    }
}