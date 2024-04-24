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
    private String introduction;

    public static OtherProfileResponse of(Member member) {
        return OtherProfileResponse.builder()
                .nickname(member.getNickname())
                .score(member.getScore())
                .imgUrl(member.getImgUrl())
                .introduction(member.getIntroduction())
                .build();
    }

    public static OtherProfileResponse defaultValue() {
        return OtherProfileResponse.builder()
                .nickname("작성자")
                .score(0)
                .imgUrl("")
                .introduction("1등이 되면 이곳에 자신의 자기소개 글을 올릴 수 있습니다.")
                .build();
    }
}
