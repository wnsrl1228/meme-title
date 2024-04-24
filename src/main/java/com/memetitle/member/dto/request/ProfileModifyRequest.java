package com.memetitle.member.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor()
@Getter
public class ProfileModifyRequest {

    @Length(max = 20, message = "닉네임은 최대 20자까지 입력할 수 있습니다.")
    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    private String imgUrl;

    @NotNull(message = "자기소개를 입력해주세요.")
    @Length(max = 250, message = "자기소개는 최대 250자까지 입력할 수 있습니다.")
    private String introduction;

    public ProfileModifyRequest(String nickname, String imgUrl, String introduction) {
        this.nickname = nickname;
        this.imgUrl = imgUrl;
        this.introduction = introduction;
    }
}
