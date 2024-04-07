package com.memetitle.member.dto.response;

import com.memetitle.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {

    private Long memberId;
    private String email;
    private String nickname;
    private String imgUrl;
    private int score;

    public static ProfileResponse of(Member member) {
        return ProfileResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .score(member.getScore())
                .imgUrl(member.getImgUrl())
                .build();
    }
}
