package com.memetitle.comment.repository;

import com.memetitle.comment.domain.Comment;
import com.memetitle.comment.domain.QComment;
import com.memetitle.comment.domain.QCommentLike;
import com.memetitle.comment.dto.CommentDto;
import com.memetitle.member.domain.QMember;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CommentCustomRepositoryImpl implements CommentCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<CommentDto> findByTitleId(Long titleId, Pageable pageable) {
        QComment comment = QComment.comment;
        QMember member = QMember.member;

        // Main query
        List<CommentDto> content = jpaQueryFactory
                .select(Projections.constructor(CommentDto.class,
                        comment.id,
                        comment.title.id,
                        comment.title.memeId,
                        comment.content,
                        comment.member.id,
                        comment.member.nickname,
                        comment.member.imgUrl,
                        comment.likeCount,
                        comment.createdAt,
                        Expressions.constant(false),
                        Expressions.constant(false)
                ))
                .from(comment)
                .leftJoin(comment.member, member)
                .where(comment.title.id.eq(titleId))
                .offset(pageable.getOffset())
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                .fetch();

        // Count query
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.title.id.eq(titleId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<CommentDto> findByTitleId(Long memberId, Long titleId, Pageable pageable) {
        QComment comment = QComment.comment;
        QCommentLike commentLike = QCommentLike.commentLike;
        QMember member = QMember.member;

        List<CommentDto> content = jpaQueryFactory
                .select(Projections.constructor(CommentDto.class,
                        comment.id,
                        comment.title.id,
                        comment.title.memeId,
                        comment.content,
                        comment.member.id,
                        comment.member.nickname,
                        comment.member.imgUrl,
                        comment.likeCount,
                        comment.createdAt,
                        new CaseBuilder()
                                .when(commentLike.id.isNull()).then(false)
                                .otherwise(true).as("isLiked"),
                        new CaseBuilder()
                                .when(comment.member.id.eq(memberId)).then(true)
                                .otherwise(false).as("isOwner")
                ))
                .from(comment)
                .leftJoin(comment.member, member)
                .leftJoin(commentLike).on(comment.id.eq(commentLike.comment.id)
                        .and(commentLike.member.id.eq(memberId)))
                .where(comment.title.id.eq(titleId))
                .offset(pageable.getOffset())
                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.title.id.eq(titleId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private List<OrderSpecifier> getOrderSpecifier(Sort sort) {
        return sort.stream()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    PathBuilder orderByExpression = new PathBuilder(Comment.class, "comment");
                    return new OrderSpecifier(direction, orderByExpression.get(order.getProperty()));
                })
                .toList();
    }
}
