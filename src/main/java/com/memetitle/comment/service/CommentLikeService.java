package com.memetitle.comment.service;

import com.memetitle.comment.domain.Comment;
import com.memetitle.comment.domain.CommentLike;
import com.memetitle.comment.repository.CommentLikeRepository;
import com.memetitle.comment.repository.CommentRepository;
import com.memetitle.global.exception.InvalidException;
import com.memetitle.member.domain.Member;
import com.memetitle.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.memetitle.global.exception.ErrorCode.*;
import static com.memetitle.global.exception.ErrorCode.DUPLICATE_TITLE_LIKE;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    public Long saveLike(final Long memberId, final Long commentId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_MEMBER_ID));
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_COMMENT_ID));

        if (comment.getMember().getId() == member.getId()) {
            throw new InvalidException(SELF_COMMENT_LIKE_DISALLOWED);
        }
        // 이미 좋아요를 누른 경우
        if (commentLikeRepository.existsByMemberAndComment(member, comment)) {
            throw new InvalidException(DUPLICATE_COMMENT_LIKE);
        }

        comment.increaseLike();

        final CommentLike commentLike = new CommentLike(member, comment);
        return commentLikeRepository.save(commentLike).getId();
    }

    public void deleteLike(final Long memberId, final Long commentId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_MEMBER_ID));
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_COMMENT_ID));


        if (comment.getMember().getId() == member.getId()) {
            throw new InvalidException(SELF_COMMENT_LIKE_DISALLOWED);
        }

        // 좋아요를 한 적이 없는 경우
        final CommentLike commentLike = commentLikeRepository.findByMemberAndComment(member, comment)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_COMMENT_LIKE));

        comment.decreaseLike();
        commentLikeRepository.delete(commentLike);
    }
}
