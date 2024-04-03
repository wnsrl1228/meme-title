package com.memetitle.meme.domain;

import com.memetitle.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Title {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memeId;

    @Column(nullable = false, length = 50)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Title(Long memeId, Member member, String title) {
        this.memeId = memeId;
        this.member = member;
        this.title = title;
    }

    public boolean isOwner(Long memberId) {
        return this.member.getId() == memberId;
    }

    public boolean isNotOwner(Long memberId) {
        return this.member.getId() != memberId;
    }

}
