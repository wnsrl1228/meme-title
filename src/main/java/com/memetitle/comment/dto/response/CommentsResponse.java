package com.memetitle.comment.dto.response;

import com.memetitle.comment.domain.Comment;
import com.memetitle.comment.dto.CommentElement;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CommentsResponse {
    private List<CommentElement> comments;
    private Integer page;
    private Integer totalPages;
    private Long totalElement;
    private Boolean isEmpty;

    public static CommentsResponse ofComments(Page<Comment> comments) {
        final List<CommentElement> commentElement = comments.stream()
                .map(CommentElement::of)
                .collect(Collectors.toList());

        comments.getTotalElements();
        comments.getTotalPages();
        long a = comments.getNumber();
        return CommentsResponse.builder()
                .comments(commentElement)
                .page(comments.getNumber())
                .totalPages(comments.getTotalPages())
                .totalElement(comments.getTotalElements())
                .isEmpty(comments.isEmpty())
                .build();
    }

}