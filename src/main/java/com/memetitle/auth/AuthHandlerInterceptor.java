package com.memetitle.auth;

import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.global.exception.AuthException;
import com.memetitle.global.exception.ErrorCode;

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
        String token = extractTokenFromRequest(request);
        jwtProvider.validateToken(token);

        final String subject = Optional.ofNullable(jwtProvider.getSubject(token))
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN));

        final Long memberId = Long.valueOf(subject);
        request.setAttribute("memberId", memberId);
        log.info("[Member ID {} accessed the resource]", memberId);

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

        throw new AuthException(ErrorCode.INVALID_TOKEN);
    }
}
