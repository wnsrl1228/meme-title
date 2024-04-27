package com.memetitle.auth.dto;

import lombok.Getter;

@Getter
public class AdminMember {

    private final Long memberId;

    public AdminMember(final Long memberId) {
        this.memberId = memberId;
    }
}
