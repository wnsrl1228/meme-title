package com.memetitle.comment.dto;

import com.memetitle.comment.domain.Comment;
import com.memetitle.mebmer.dto.response.MemberResponse;
import com.memetitle.meme.domain.Title;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentElement {

    private Long id;
    private Long titleId;
    private String content;
    private MemberResponse member;
    private LocalDateTime createdAt;

    public static CommentElement of(Comment comment) {
        return CommentElement.builder()
                .id(comment.getId())
                .titleId(comment.getTitle().getId())
                .content(comment.getContent())
                .member(MemberResponse.of(comment.getMember()))
                .createdAt(comment.getCreatedAt())
                .build();

    }
}