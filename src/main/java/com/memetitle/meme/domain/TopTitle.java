package com.memetitle.meme.domain;

import com.memetitle.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopTitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memeId;

    @Column(nullable = false)
    private Long titleId;

    @Column(nullable = false, length = 50)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int periodLikeCount;

    @Column(nullable = false)
    private int ranking;

    public TopTitle(Long memeId, Long titleId, String title, Member member, LocalDateTime createdAt, int periodLikeCount, int ranking) {
        this.memeId = memeId;
        this.titleId = titleId;
        this.title = title;
        this.member = member;
        this.createdAt = createdAt;
        this.periodLikeCount = periodLikeCount;
        this.ranking = ranking;
    }

    public static TopTitle of(Long memeId, Title title, int ranking) {
        return new TopTitle(
                memeId,
                title.getId(),
                title.getTitle(),
                title.getMember(),
                title.getCreatedAt(),
                title.getLikeCount(),
                ranking
        );
    }
}