package com.memetitle.member.service;

import com.memetitle.comment.domain.Comment;
import com.memetitle.comment.dto.response.CommentsResponse;
import com.memetitle.comment.repository.CommentRepository;
import com.memetitle.global.exception.ErrorCode;
import com.memetitle.global.exception.InvalidException;
import com.memetitle.member.domain.Member;
import com.memetitle.member.dto.request.ProfileModifyRequest;
import com.memetitle.member.dto.response.OtherProfileResponse;
import com.memetitle.member.dto.response.ProfileResponse;
import com.memetitle.member.repository.MemberRepository;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.dto.response.TitlesResponse;
import com.memetitle.meme.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.memetitle.global.exception.ErrorCode.DUPLICATE_NICKNAME;
import static com.memetitle.global.exception.ErrorCode.NOT_FOUND_MEMBER_ID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TitleRepository titleRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));

        return ProfileResponse.of(member);
    }

    @Transactional(readOnly = true)
    public OtherProfileResponse getOtherProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));

        return OtherProfileResponse.of(member);
    }

    public void updateProfile(Long memberId, ProfileModifyRequest profileModifyRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_MEMBER_ID));

        final String newNickname = profileModifyRequest.getNickname();
        if (member.getNickname() != newNickname) {
            validateNicknameUniqueness(newNickname);
            member.updateNickname(newNickname);
        }

        final String newImgUrl = profileModifyRequest.getImgUrl();
        if (member.getImgUrl() != newImgUrl) {
            member.updateImgUrl(newImgUrl);
        }
    }

    @Transactional(readOnly = true)
    public TitlesResponse getTitlesByMemberId(Long memberId) {
        if(!memberRepository.existsById(memberId)) {
            throw new InvalidException(NOT_FOUND_MEMBER_ID);
        }
        List<Title> titles = titleRepository.findByMemberId(memberId);

        return TitlesResponse.ofTitles(titles);
    }

    @Transactional(readOnly = true)
    public CommentsResponse getCommentsByMemberId(Long memberId) {
        if(!memberRepository.existsById(memberId)) {
            throw new InvalidException(NOT_FOUND_MEMBER_ID);
        }
        final List<Comment> comments = commentRepository.findByMemberId(memberId);

        return CommentsResponse.ofComments(comments);
    }

    private void validateNicknameUniqueness(String newNickname) {
        if (memberRepository.existsByNickname(newNickname)) {
            throw new InvalidException(DUPLICATE_NICKNAME);
        }
    }

}
