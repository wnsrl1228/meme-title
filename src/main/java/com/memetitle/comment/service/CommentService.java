package com.memetitle.comment.service;

import com.memetitle.comment.domain.Comment;
import com.memetitle.comment.dto.request.CommentCreateRequest;
import com.memetitle.comment.dto.request.CommentModifyRequest;
import com.memetitle.comment.dto.response.CommentsResponse;
import com.memetitle.comment.repository.CommentRepository;
import com.memetitle.global.exception.InvalidException;
import com.memetitle.mebmer.domain.Member;
import com.memetitle.mebmer.repository.MemberRepository;
import com.memetitle.meme.domain.Title;
import com.memetitle.meme.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.memetitle.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final TitleRepository titleRepository;

    public Long saveComment(final Long memberId, final Long titleId, final CommentCreateRequest commentCreateRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_MEMBER_ID));
        final Title title = titleRepository.findById(titleId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_TITLE_ID));

        final Comment comment = new Comment(
                commentCreateRequest.getContent(),
                member,
                title
        );
        return commentRepository.save(comment).getId();
    }

    @Transactional(readOnly = true)
    public CommentsResponse getCommentsByTitleId(final Long titleId) {
        if(!titleRepository.existsById(titleId)) {
            throw new InvalidException(NOT_FOUND_TITLE_ID);
        }

        final List<Comment> comments = commentRepository.findByTitleId(titleId);
        return CommentsResponse.ofComments(comments);
    }

    public void updateComment(final Long memberId, final Long commentId, final CommentModifyRequest commentModifyRequest) {
        if(!memberRepository.existsById(memberId)) {
            throw new InvalidException(NOT_FOUND_TITLE_ID);
        }
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_COMMENT_ID));

        if (comment.isNotOwner(memberId)) {
            throw new InvalidException(COMMENT_ACCESS_DENIED);
        }

        comment.updateContent(commentModifyRequest.getContent());
    }

    public void deleteComment(final Long memberId, final Long commentId) {
        if(!memberRepository.existsById(memberId)) {
            throw new InvalidException(NOT_FOUND_TITLE_ID);
        }
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new InvalidException(NOT_FOUND_COMMENT_ID));

        if (comment.isNotOwner(memberId)) {
            throw new InvalidException(COMMENT_ACCESS_DENIED);
        }

        commentRepository.deleteById(commentId);
    }
}
