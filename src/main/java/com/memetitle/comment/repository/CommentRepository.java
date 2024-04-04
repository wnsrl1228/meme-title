package com.memetitle.comment.repository;

import com.memetitle.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByTitleId(Long titleId, Pageable pageable);

    Page<Comment> findByMemberId(Long memberId, Pageable pageable);
}
