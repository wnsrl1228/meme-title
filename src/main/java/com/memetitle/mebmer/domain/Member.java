package com.memetitle.mebmer.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String snsLoginId;

    @Column(nullable = false, unique = true)
    private String snsTokenId;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    private String imgUrl;

    @Column(nullable = false)
    private int score;

    @Enumerated(value = EnumType.STRING)
    private MemberState status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Member(String snsLoginId, String snsTokenId, String nickname) {
        this.snsLoginId = snsLoginId;
        this.snsTokenId = snsTokenId;
        this.nickname = nickname;
    }
}
