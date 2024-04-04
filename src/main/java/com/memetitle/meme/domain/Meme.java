package com.memetitle.meme.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imgOriginalName;

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    public Meme(String imgOriginalName, String imgUrl, LocalDateTime startDate, LocalDateTime endDate) {
        this.imgOriginalName = imgOriginalName;
        this.imgUrl = imgUrl;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
