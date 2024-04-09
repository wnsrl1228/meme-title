package com.memetitle.member.service;

import com.memetitle.comment.domain.Comment;
import com.memetitle.comment.dto.response.CommentsResponse;
import com.memetitle.comment.repository.CommentRepository;
import com.memetitle.global.exception.InvalidException;
import com.memetitle.member.domain.Member;
import com.memetitle.member.dto.RankDto;
import com.memetitle.member.dto.request.ProfileModifyRequest;
import com.memetitle.member.dto.response.OtherProfileResponse;
import com.memetitle.member.dto.response.ProfileResponse;
import com.memetitle.member.dto.response.RankingResponse;
import com.memetitle.member.repository.MemberRepository;
import com.memetitle.meme.domain.Meme;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.domain.TopTitle;
import com.memetitle.meme.dto.response.TitlesResponse;
import com.memetitle.meme.repository.MemeRepository;
import com.memetitle.meme.repository.TitleRepository;
import com.memetitle.meme.repository.TopTitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final MemeRepository memeRepository;
    private final TopTitleRepository topTitleRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));

        return ProfileResponse.of(member);
    }

    @Transactional(readOnly = true)
    public OtherProfileResponse getOtherProfile(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));

        return OtherProfileResponse.of(member);
    }

    public void updateProfile(final Long memberId, final ProfileModifyRequest profileModifyRequest) {
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
    public TitlesResponse getPageableTitlesByMemberId(final Long memberId, final Pageable pageable) {
        if(!memberRepository.existsById(memberId)) {
            throw new InvalidException(NOT_FOUND_MEMBER_ID);
        }
        final Slice<Title> titles = titleRepository.findByMemberId(memberId, pageable);

        return TitlesResponse.ofTitles(titles);
    }

    @Transactional(readOnly = true)
    public CommentsResponse getPageableCommentsByMemberId(final Long memberId, final Pageable pageable) {
        if(!memberRepository.existsById(memberId)) {
            throw new InvalidException(NOT_FOUND_MEMBER_ID);
        }
        final Page<Comment> comments = commentRepository.findByMemberId(memberId, pageable);

        return CommentsResponse.ofComments(comments);
    }

    @Transactional(readOnly = true)
    public RankingResponse getPageableMembersRanking(Pageable pageable) {
        Page<RankDto> rankDtos = memberRepository.findMembersRanking(pageable);
        return RankingResponse.ofRankDto(rankDtos);
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 0시에
    public void updateScoreByTitleLikeCount() {

        // 종료일이 오늘 날찌인 meme을 가져옴
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0));
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,0,0));
        List<Meme> memes = memeRepository.findAllByEndDateBetween(startTime, endTime);


        // 없을 경우 종료
        if (memes.isEmpty()) {
            return;
        }

        // 있을 경우 해당 meme의 title을 likeCount가 많은 순서로 5개만 가져옴
        for (Meme meme : memes) {

            // meme 상태를 ENDED로 변경
            meme.updateStatusToEnded();

            // 해당 meme의 title을 좋아요 개수가 많은 순서로 모두 가져옴
            List<Title> titles = titleRepository.findByMemeIdOrderByLikeCountDesc(meme.getId());

            if (titles.isEmpty()) continue;

            // 상위 5개의 title을 작성한 member에게 점수 부여
            int beforeLikeCount = titles.get(0).getLikeCount();
            int point = 100;
            for (Title title : titles) {
                // 좋아요 수가 0일 경우 점수 X
                if (title.getLikeCount() == 0) break;

                // 좋아요 수가 동일할 경우 같은 순위에 같은 점수 부여
                if (beforeLikeCount == title.getLikeCount()) {
                    title.getMember().plusScore(point);
                    continue;
                }
                // 5등까지 모두 점수를 준 경우
                if (point == 20) break;
                point -= 20;
                title.getMember().plusScore(point);
                beforeLikeCount = title.getLikeCount();
            }

            // 상위 3개의 제목은 TopTitle에 저장
            int rank = 1;
            beforeLikeCount = titles.get(0).getLikeCount();
            for (int i = 0; i < Math.min(3, titles.size()); i++) {

                Title title = titles.get(i);

                if (beforeLikeCount == title.getLikeCount()) {
                    topTitleRepository.save(TopTitle.of(meme.getId(), title, rank));
                    continue;
                }
                topTitleRepository.save(TopTitle.of(meme.getId(), title, ++rank));
            }
        }
    }

    private void validateNicknameUniqueness(final String newNickname) {
        if (memberRepository.existsByNickname(newNickname)) {
            throw new InvalidException(DUPLICATE_NICKNAME);
        }
    }
}
