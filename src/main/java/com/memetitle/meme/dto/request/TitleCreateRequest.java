package com.memetitle.meme.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor()
@Getter
public class TitleCreateRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    @Length(max = 50, message = "제목을 50자 이내로 입력해주세요.")
    private String title;

    public TitleCreateRequest(String title) {
        this.title = title;
    }
}
