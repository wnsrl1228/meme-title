package com.memetitle.member.dto.response;

import com.memetitle.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OtherProfileResponse {

    private String nickname;
    private String imgUrl;
    private int score;

    public static OtherProfileResponse of(Member member) {
        return OtherProfileResponse.builder()
                .nickname(member.getNickname())
                .score(member.getScore())
                .imgUrl(member.getImgUrl())
                .build();
    }
}
