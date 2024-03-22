package com.memetitle.auth.presentation;

import com.memetitle.auth.dto.LoginTokens;
import com.memetitle.auth.dto.response.TokenResponse;
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
    public ResponseEntity<TokenResponse> login(
            @RequestParam("code") final String code,
            final HttpServletResponse response
    ) {
        final LoginTokens token = loginService.login(code);

        final ResponseCookie cookie1 = ResponseCookie.from("refresh-token", token.getRefreshToken())
                .maxAge(600)
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();

        response.addHeader(SET_COOKIE, cookie1.toString());

        return ResponseEntity.ok().body(new TokenResponse(token.getAccessToken()));
    }

    @GetMapping("/auth/token")
    public ResponseEntity<TokenResponse> refreshAccessToken(
            @CookieValue("refresh-token") final String refreshToken
    ) {
        final String accessToken = loginService.renewAccessToken(refreshToken);
        return ResponseEntity.ok().body(new TokenResponse(accessToken));
    }
}
