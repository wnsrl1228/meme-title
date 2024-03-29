package com.memetitle.mebmer.presentation;

import com.memetitle.auth.Login;
import com.memetitle.auth.dto.LoginMember;
import com.memetitle.mebmer.dto.response.ProfileResponse;
import com.memetitle.mebmer.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("member/profile")
    public ResponseEntity<ProfileResponse> getMemberProfile(
            @Login final LoginMember loginMember
    ) {

        final ProfileResponse profile = memberService.getProfile(loginMember.getMemberId());
        return ResponseEntity.ok(profile);
    }
}
