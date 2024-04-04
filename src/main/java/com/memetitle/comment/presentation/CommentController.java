package com.memetitle.comment.presentation;

import com.memetitle.auth.Login;
import com.memetitle.auth.dto.LoginMember;
import com.memetitle.comment.dto.request.CommentCreateRequest;
import com.memetitle.comment.dto.request.CommentModifyRequest;
import com.memetitle.comment.dto.response.CommentsResponse;
import com.memetitle.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/titles/{titleId}/comments")
    public ResponseEntity<Void> createComment(
            @PathVariable final Long titleId,
            @Valid @RequestBody final CommentCreateRequest commentCreateRequest,
            @Login final LoginMember loginMember

    ) {
        commentService.saveComment(loginMember.getMemberId(), titleId, commentCreateRequest);
        return ResponseEntity.created(URI.create("/titles/" + titleId + "/comments")).build();
    }

    @GetMapping("/titles/{titleId}/comments")
    public ResponseEntity<CommentsResponse> getCommentsForTitle(
            @PathVariable final Long titleId,
            @PageableDefault(sort = "createdAt", direction = DESC) final Pageable pageable
    ) {
        return ResponseEntity.ok().body(commentService.getPageableCommentsByTitleId(titleId, pageable));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> modifyComment(
            @PathVariable final Long commentId,
            @Valid @RequestBody final CommentModifyRequest commentModifyRequest,
            @Login final LoginMember loginMember
    ) {
        commentService.updateComment(loginMember.getMemberId(), commentId, commentModifyRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable final Long commentId,
            @Login final LoginMember loginMember
    ) {
        commentService.deleteComment(loginMember.getMemberId(), commentId);
        return ResponseEntity.noContent().build();
    }
}
