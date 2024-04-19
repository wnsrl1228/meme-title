package com.memetitle.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private Long titleId;
    private Long memeId;
    private String content;
    private Long memberId;
    private String nickname;
    private String imgUrl;
    private int likeCount;
    private LocalDateTime createdAt;
    private Boolean isLiked;
    private Boolean isOwner;

}