package com.memetitle.meme.service;

import com.memetitle.global.exception.InvalidException;
import com.memetitle.member.domain.Member;
import com.memetitle.member.repository.MemberRepository;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.dto.request.TitleCreateRequest;
import com.memetitle.meme.dto.response.TitleDetailResponse;
import com.memetitle.meme.dto.response.TitlesResponse;
import com.memetitle.meme.repository.MemeRepository;
import com.memetitle.meme.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.memetitle.global.exception.ErrorCode.*;

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
        if (!memeRepository.existsById(memeId)) {
            throw new InvalidException(NOT_FOUND_MEME_ID);
        }
        final Title title = new Title(
                memeId,
                member,
                titleCreateRequest.getTitle()
        );

        return titleRepository.save(title).getId();
    }

    @Transactional(readOnly = true)
    public TitlesResponse getPageableTitlesByMemeId(final Long memeId, final Pageable pageable) {
        if (!memeRepository.existsById(memeId)) {
            throw new InvalidException(NOT_FOUND_MEME_ID);
        }
        final Slice<Title> titles = titleRepository.findByMemeId(memeId, pageable);
        return TitlesResponse.ofTitles(titles);
    }

    @Transactional(readOnly = true)
    public TitleDetailResponse getTitleById(final Long titleId) {
        final Title title = titleRepository.findById(titleId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_TITLE_ID));

        return TitleDetailResponse.of(title);
    }

    public void deleteTitle(final Long memberId, final Long titleId) {
        if(!memberRepository.existsById(memberId)) {
            throw new InvalidException(NOT_FOUND_MEMBER_ID);
        }
        final Title title = titleRepository.findById(titleId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_TITLE_ID));

        if (title.isNotOwner(memberId)) {
            throw new InvalidException(TITLE_ACCESS_DENIED);
        }

        titleRepository.deleteById(titleId);
    }

}
