package com.memetitle.auth.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class LoginRequest {

    @NotBlank(message = "잘못된 이메일입니다.")
    private String loginId;

    @NotBlank(message = "잘못된 비밀번호입니다.")
    private String password;
}
