package com.memetitle.comment.dto;

import com.memetitle.comment.domain.Comment;
import com.memetitle.member.dto.response.MemberResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentElement {

    private Long id;
    private Long titleId;
    private Long memeId;
    private String content;
    private MemberResponse member;
    private int likeCount;
    private LocalDateTime createdAt;
    private Boolean isOwner;
    private Boolean isLiked;

    public static CommentElement of(Comment comment) {
        return CommentElement.builder()
                .id(comment.getId())
                .memeId(comment.getTitle().getMemeId())
                .titleId(comment.getTitle().getId())
                .content(comment.getContent())
                .member(MemberResponse.of(comment.getMember()))
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .isOwner(null)
                .isLiked(null)
                .build();
    }

    public static CommentElement of(CommentDto commentDto) {
        MemberResponse memberResponse = MemberResponse.builder()
                .id(commentDto.getMemberId())
                .nickname(commentDto.getNickname())
                .imgUrl(commentDto.getImgUrl())
                .build();

        return CommentElement.builder()
                .id(commentDto.getId())
                .titleId(commentDto.getTitleId())
                .memeId(commentDto.getMemeId())
                .content(commentDto.getContent())
                .member(memberResponse)
                .likeCount(commentDto.getLikeCount())
                .createdAt(commentDto.getCreatedAt())
                .isOwner(commentDto.getIsOwner())
                .isLiked(commentDto.getIsLiked())
                .build();

    }
}