package com.memetitle.auth;

import com.memetitle.auth.infrastructure.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AuthHandlerInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("AuthHandlerInterceptor URL : " + request.getRequestURI());

        String token = extractTokenFromRequest(request);
        final Jws<Claims> claimsJws = jwtProvider.validateToken(token);

        final String subject = Optional.ofNullable(claimsJws.getPayload().getSubject())
                .orElseThrow(() -> new RuntimeException("잘못된 토큰입니다."));

        final Long memberId = Long.valueOf(subject);
        request.setAttribute("memberId", memberId);

        return true;
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        // Authorization 헤더 값을 가져옵니다.
        String authorizationHeader = request.getHeader("Authorization");

        // 헤더가 존재하고, Bearer로 시작하는지 확인합니다.
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Bearer 토큰을 추출하여 반환합니다.
            return authorizationHeader.substring(7); // "Bearer " 다음의 값부터 반환합니다.
        }

        throw new RuntimeException("유효하지 않은 토큰입니다.");
    }
}
