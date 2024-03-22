package com.memetitle.auth;

import com.memetitle.auth.infrastructure.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AuthHandlerInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("AuthHandlerInterceptor");

        final Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new RuntimeException("로그인 후 이용해주세요.");
        }

        final String token = Arrays.stream(cookies)
                .filter(c -> c.getName().equals("access-token"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("토큰이 존재하지 않습니다."))
                .getValue();

        final Jws<Claims> claimsJws = jwtProvider.validateToken(token);

        final String subject = Optional.ofNullable(claimsJws.getPayload().getSubject())
                .orElseThrow(() -> new RuntimeException("잘못된 토큰입니다."));

        final Long memberId = Long.valueOf(subject);

        request.setAttribute("memberId", memberId);

        return true;
    }
}
