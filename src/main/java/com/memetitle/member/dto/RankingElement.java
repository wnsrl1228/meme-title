package com.memetitle.member.dto;

import com.memetitle.member.dto.response.MemberResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RankingElement {

    private int rank;
    private MemberResponse member;
    private int score;

    public static RankingElement of(RankDto rankDto) {

        MemberResponse member = MemberResponse.builder()
                .id(rankDto.getMemberId())
                .nickname(rankDto.getNickname())
                .imgUrl(rankDto.getImgUrl())
                .build();

        return RankingElement.builder()
                .rank(rankDto.getRanking())
                .member(member)
                .score(rankDto.getScore())
                .build();
    }

}
