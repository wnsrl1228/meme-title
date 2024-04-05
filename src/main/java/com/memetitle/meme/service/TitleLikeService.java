package com.memetitle.meme.service;

import com.memetitle.global.exception.ErrorCode;
import com.memetitle.global.exception.InvalidException;
import com.memetitle.member.domain.Member;
import com.memetitle.member.repository.MemberRepository;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.domain.TitleLike;
import com.memetitle.meme.repository.TitleLikeRepository;
import com.memetitle.meme.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.memetitle.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TitleLikeService {

    private final TitleLikeRepository titleLikeRepository;
    private final MemberRepository memberRepository;
    private final TitleRepository titleRepository;

    public Long saveLike(Long memberId, Long titleId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_MEMBER_ID));
        Title title = titleRepository.findById(titleId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_TITLE_ID));

        if (title.getMember().getId() == member.getId()) {
            throw new InvalidException(SELF_TITLE_LIKE_DISALLOWED);
        }
        // 이미 좋아요를 누른 경우
        if (titleLikeRepository.existsByMemberAndTitle(member, title)) {
            throw new InvalidException(DUPLICATE_TITLE_LIKE);
        }

        title.increaseLike();

        TitleLike titleLike = new TitleLike(member, title);
        return titleLikeRepository.save(titleLike).getId();
    }

    public void deleteLike(Long memberId, Long titleId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_MEMBER_ID));
        Title title = titleRepository.findById(titleId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_TITLE_ID));

        if (title.getMember().getId() == member.getId()) {
            throw new InvalidException(SELF_TITLE_LIKE_DISALLOWED);
        }

        // 좋아요를 한 적이 없는 경우
        TitleLike titleLike = titleLikeRepository.findByMemberAndTitle(member, title)
                        .orElseThrow(() -> new InvalidException(NOT_FOUND_TITLE_LIKE));

        title.decreaseLike();
        titleLikeRepository.delete(titleLike);
    }

}
