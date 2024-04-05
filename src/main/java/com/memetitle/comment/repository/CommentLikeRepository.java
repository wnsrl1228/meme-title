package com.memetitle.comment.repository;

import com.memetitle.comment.domain.Comment;
import com.memetitle.comment.domain.CommentLike;
import com.memetitle.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByMemberAndComment(Member member, Comment comment);

    Optional<CommentLike> findByMemberAndComment(Member member, Comment comment);

    boolean existsByMemberIdAndComment(Long memberId, Comment comment);
}
