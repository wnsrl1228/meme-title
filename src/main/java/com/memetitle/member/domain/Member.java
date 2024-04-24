package com.memetitle.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String snsTokenId;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    private String imgUrl;

    @Column(nullable = false)
    private int score;

    @Enumerated(value = EnumType.STRING)
    private MemberState status;

    @Column(nullable = false)
    private String introduction = "";

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Member(String snsTokenId, String email, String nickname) {
        this.snsTokenId = snsTokenId;
        this.email = email;
        this.nickname = nickname;
        status = MemberState.ACTIVE;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void updateScore(int score) {
        this.score = score;
    }

    public void plusScore(int score) {
        this.score += score;
    }
}
