package com.memetitle.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class LoginToken {

    private String accessToken;

    public LoginToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
