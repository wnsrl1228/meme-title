package com.memetitle.comment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor()
@Getter
public class CommentCreateRequest {

    @NotBlank(message = "댓글을 입력해주세요.")
    private String content;

    public CommentCreateRequest(String content) {
        this.content = content;
    }
}
