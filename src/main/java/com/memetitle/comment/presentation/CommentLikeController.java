package com.memetitle.comment.presentation;

import com.memetitle.auth.Login;
import com.memetitle.auth.dto.LoginMember;
import com.memetitle.comment.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> createLike(
            @PathVariable final Long commentId,
            @Login final LoginMember loginMember
    ) {
        commentLikeService.saveLike(loginMember.getMemberId(), commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> deleteLike(
            @PathVariable final Long commentId,
            @Login final LoginMember loginMember
    ) {
        commentLikeService.deleteLike(loginMember.getMemberId(), commentId);
        return ResponseEntity.noContent().build();
    }

}
