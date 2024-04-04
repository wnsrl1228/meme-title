package com.memetitle.comment.repository;

import com.memetitle.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"member"})
    Page<Comment> findByTitleId(Long titleId, Pageable pageable);

    @EntityGraph(attributePaths = {"member"})
    Page<Comment> findByMemberId(Long memberId, Pageable pageable);
}
