package com.memetitle.meme.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

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
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    public Meme(String imgOriginalName, String imgUrl, LocalDate startDate, LocalDate endDate) {
        this.imgOriginalName = imgOriginalName;
        this.imgUrl = imgUrl;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
