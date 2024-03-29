package com.memetitle.meme.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor()
@Getter
public class TitleCreateRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    public TitleCreateRequest(String title) {
        this.title = title;
    }
}
