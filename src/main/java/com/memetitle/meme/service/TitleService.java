package com.memetitle.meme.service;

import com.memetitle.auth.dto.LoginMember;
import com.memetitle.global.exception.InvalidException;
import com.memetitle.mebmer.domain.Member;
import com.memetitle.mebmer.repository.MemberRepository;
import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.dto.request.TitleCreateRequest;
import com.memetitle.meme.dto.response.TitlesResponse;
import com.memetitle.meme.repository.MemeRepository;
import com.memetitle.meme.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.memetitle.global.exception.ErrorCode.NOT_FOUND_MEMBER_ID;
import static com.memetitle.global.exception.ErrorCode.NOT_FOUND_MEME_ID;

@Service
@Transactional
@RequiredArgsConstructor
public class TitleService {

    private final TitleRepository titleRepository;
    private final MemeRepository memeRepository;
    private final MemberRepository memberRepository;

    public Long saveTitle(final Long memberId, final Long memeId, final TitleCreateRequest titleCreateRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_MEMBER_ID));
        memeRepository.findById(memeId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_MEME_ID));

        final Title title = new Title(
                memeId,
                member,
                titleCreateRequest.getTitle()
        );

        return titleRepository.save(title).getId();
    }

    public TitlesResponse getTitlesByMemeId(final Long memeId) {
        memeRepository.findById(memeId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_MEME_ID));

        final List<Title> titles = titleRepository.findByMemeId(memeId);

        return TitlesResponse.ofTitles(titles);
    }

}
