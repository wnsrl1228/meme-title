package com.memetitle.mebmer.dto.response;

import com.memetitle.mebmer.domain.Member;
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
