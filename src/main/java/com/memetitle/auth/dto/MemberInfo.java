package com.memetitle.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.*;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class MemberInfo  {

    private String snsTokenId;

    private String email;

    private String nickname;

    public MemberInfo(String snsTokenId, String email, String nickname) {
        this.snsTokenId = snsTokenId;
        this.email = email;
        this.nickname = nickname;
    }

}