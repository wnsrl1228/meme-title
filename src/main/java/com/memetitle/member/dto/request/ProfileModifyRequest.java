package com.memetitle.member.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor()
@Getter
public class ProfileModifyRequest {

    @Length(max = 20, message = "닉네임은 최대 20자까지 입력할 수 있습니다.")
    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    private String imgUrl;

    public ProfileModifyRequest(String nickname, String imgUrl) {
        this.nickname = nickname;
        this.imgUrl = imgUrl;
    }
}
