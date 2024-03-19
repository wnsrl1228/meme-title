package com.memetitle.auth.presentation;

import com.memetitle.auth.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login/kakao")
    public ResponseEntity<Void> login(
            @RequestParam("code") final String code
    ) {
        /**
         * 로그인 이후 토큰을 반환해야됨
         */
        loginService.login(code);
        return ResponseEntity.ok().build();
    }
}
