package com.memetitle.comment.domain;

import com.memetitle.member.domain.Member;
import com.memetitle.meme.domain.Title;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "title_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Title title;

    @Column(nullable = false)
    private int likeCount;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Comment(String content, Member member, Title title) {
        this.content = content;
        this.member = member;
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public boolean isOwner(Long memberId) {
        return this.member.getId() == memberId;
    }

    public boolean isNotOwner(Long memberId) {
        return this.member.getId() != memberId;
    }

    public void increaseLike() {
        this.likeCount++;
    }
    public void decreaseLike() {
        this.likeCount--;
    }
}