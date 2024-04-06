package com.memetitle.comment.repository;

import com.memetitle.comment.domain.Comment;
import com.memetitle.comment.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT new com.memetitle.comment.dto.CommentDto(c.id, c.title.id, c.content, c.member.id, c.member.nickname, c.member.imgUrl,  c.likeCount, c.createdAt, false, false)" +
            " from Comment c LEFT JOIN c.member m " +
            " WHERE c.title.id = :titleId"
            ,countQuery = "select count(c) from Comment c where c.title.id = :titleId")
    Page<CommentDto> findByTitleId(Long titleId, Pageable pageable);

    @Query(value = "SELECT new com.memetitle.comment.dto.CommentDto(c.id, c.title.id, c.content, c.member.id, c.member.nickname, c.member.imgUrl,  c.likeCount, c.createdAt, (CASE WHEN cl.id IS NULL THEN false ELSE true END), (CASE WHEN c.member.id = :memberId THEN true ELSE false END))" +
            " from Comment c LEFT JOIN c.member m " +
            " LEFT JOIN CommentLike cl ON c.id = cl.comment.id AND cl.member.id = :memberId" +
            " WHERE c.title.id = :titleId"
    ,countQuery = "select count(c) from Comment c where c.title.id = :titleId")
    Page<CommentDto> findByTitleId(Long memberId, Long titleId, Pageable pageable);

    @EntityGraph(attributePaths = {"member"})
    Page<Comment> findByMemberId(Long memberId, Pageable pageable);
}
