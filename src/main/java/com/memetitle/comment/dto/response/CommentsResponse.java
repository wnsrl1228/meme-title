package com.memetitle.comment.dto.response;

import com.memetitle.comment.domain.Comment;
import com.memetitle.comment.dto.CommentElement;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CommentsResponse {
    private List<CommentElement> comments;

    public static CommentsResponse ofComments(List<Comment> comments) {
        final List<CommentElement> commentElement = comments.stream()
                .map(CommentElement::of)
                .collect(Collectors.toList());

        return CommentsResponse.builder()
                .comments(commentElement)
                .build();
    }
}