package com.memetitle.auth.presentation;

import com.memetitle.auth.dto.LoginToken;
import com.memetitle.auth.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login/kakao")
    public ResponseEntity<Void> login(
            @RequestParam("code") final String code,
            final HttpServletResponse response
    ) {
        LoginToken token = loginService.login(code);

        final ResponseCookie cookie = ResponseCookie.from("access-token", token.getAccessToken())
                .maxAge(60)
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();
        response.addHeader(SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }
}
