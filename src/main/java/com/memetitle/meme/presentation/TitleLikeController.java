package com.memetitle.meme.presentation;

import com.memetitle.auth.Login;
import com.memetitle.auth.dto.LoginMember;
import com.memetitle.meme.dto.response.TitleDetailResponse;
import com.memetitle.meme.service.TitleLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TitleLikeController {

    private final TitleLikeService titleLikeService;

    @PostMapping("/titles/{titleId}/like")
    public ResponseEntity<TitleDetailResponse> createLike(
            @PathVariable final Long titleId,
            @Login LoginMember loginMember
    ) {
        titleLikeService.saveLike(loginMember.getMemberId(), titleId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/titles/{titleId}/like")
    public ResponseEntity<TitleDetailResponse> deleteLike(
            @PathVariable final Long titleId,
            @Login LoginMember loginMember
    ) {
        titleLikeService.deleteLike(loginMember.getMemberId(), titleId);
        return ResponseEntity.noContent().build();
    }
}
