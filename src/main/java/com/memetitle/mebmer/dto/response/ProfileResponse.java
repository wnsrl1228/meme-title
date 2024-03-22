package com.memetitle.mebmer.dto.response;

import com.memetitle.mebmer.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {

    private String email;
    private String nickname;
    private String imgUrl;
    private int score;

    public static ProfileResponse of(Member member) {
        return ProfileResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .score(member.getScore())
                .imgUrl(member.getImgUrl())
                .build();
    }
}
