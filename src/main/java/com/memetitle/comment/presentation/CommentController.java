package com.memetitle.comment.presentation;

import com.memetitle.auth.Login;
import com.memetitle.auth.dto.LoginMember;
import com.memetitle.comment.service.CommentService;
import com.memetitle.meme.dto.request.TitleCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

//    @PostMapping("/titles/{titleId}/comments")
//    public ResponseEntity<Void> createComment(
//            @PathVariable final Long titleId,
//            @Valid @RequestBody final CommentCreateRequest commentCreateRequest,
//            @Login final LoginMember loginMember
//
//    ) {
//        final Long titleId = commentService.saveComment(loginMember.getMemberId(), titleId, commentCreateRequest);
//        return ResponseEntity.created(URI.create("/memes/" + memeId + "/titles/" + titleId)).build();
//    }

}
