package com.memetitle.auth.dto;

import lombok.Getter;

@Getter
public class LoginMember {

    private final Long memberId;

    public LoginMember(final Long memberId) {
        this.memberId = memberId;
    }
}
