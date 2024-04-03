package com.memetitle.member.dto.response;

import com.memetitle.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String nickname;
    private String imgUrl;

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .imgUrl(member.getImgUrl())
                .build();
    }

}
