package com.memetitle.auth.presentation;

import com.memetitle.auth.dto.OauthToken;
import com.memetitle.auth.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

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
