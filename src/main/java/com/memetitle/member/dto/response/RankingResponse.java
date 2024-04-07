package com.memetitle.member.dto.response;

import com.memetitle.member.dto.RankDto;
import com.memetitle.member.dto.RankingElement;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class RankingResponse {
    private List<RankingElement> ranks;
    private Integer page;
    private Integer totalPages;
    private Long totalElement;
    private Boolean isEmpty;

    public static RankingResponse ofRankDto(Page<RankDto> rankDtos) {
        final List<RankingElement> rankingElements = rankDtos.stream()
                .map(RankingElement::of)
                .collect(Collectors.toList());

        return RankingResponse.builder()
                .ranks(rankingElements)
                .page(rankDtos.getNumber())
                .totalPages(rankDtos.getTotalPages())
                .totalElement(rankDtos.getTotalElements())
                .isEmpty(rankDtos.isEmpty())
                .build();
    }
}
