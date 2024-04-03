package com.memetitle.member.presentation;

import com.memetitle.auth.Login;
import com.memetitle.auth.dto.LoginMember;
import com.memetitle.comment.dto.response.CommentsResponse;
import com.memetitle.member.dto.request.ProfileModifyRequest;
import com.memetitle.member.dto.response.OtherProfileResponse;
import com.memetitle.member.dto.response.ProfileResponse;
import com.memetitle.member.service.MemberService;
import com.memetitle.meme.dto.response.TitlesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/member/profile")
    public ResponseEntity<ProfileResponse> getMemberProfile(
            @Login final LoginMember loginMember
    ) {
        return ResponseEntity.ok(memberService.getProfile(loginMember.getMemberId()));
    }

    @GetMapping("/member/profile/{memberId}")
    public ResponseEntity<OtherProfileResponse> getOtherMemberProfile(
            @PathVariable final Long memberId
    ) {
        return ResponseEntity.ok(memberService.getOtherProfile(memberId));
    }

    @PatchMapping("/member/profile")
    public ResponseEntity<Void> modifyProfile(
            @RequestBody @Valid final ProfileModifyRequest profileModifyRequest,
            @Login final LoginMember loginMember
    ) {
        memberService.updateProfile(loginMember.getMemberId(), profileModifyRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/member/titles")
    public ResponseEntity<TitlesResponse> getTitlesForMember(
            @Login final LoginMember loginMember
    ) {
        return ResponseEntity.ok().body(memberService.getTitlesByMemberId(loginMember.getMemberId()));
    }

    @GetMapping("/member/comments")
    public ResponseEntity<CommentsResponse> getCommentsForMember(
            @Login final LoginMember loginMember
    ) {
        return ResponseEntity.ok().body(memberService.getCommentsByMemberId(loginMember.getMemberId()));
    }
}
