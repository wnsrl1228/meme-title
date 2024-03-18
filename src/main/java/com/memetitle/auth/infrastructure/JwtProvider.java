package com.memetitle.auth.infrastructure;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final String issuer;
    private final Long accessExpirationTime;

    public JwtProvider(
            @Value("${jwt.secret-key}") final String secretKey,
            @Value("${jwt.claims.issuer}") final String issuer,
            @Value("${jwt.access-expiration-time}") final Long accessExpirationTime
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.issuer = issuer;
        this.accessExpirationTime = accessExpirationTime;
    }

    public String createToken(String subject) {
        final Date now = new Date();
        final Date expirationTime = new Date(now.getTime() + accessExpirationTime);

        return Jwts.builder()
                .issuer(issuer)  // 발급자
                .subject(subject) // 토큰 제목
                .issuedAt(now)  // 발급 시간
                .expiration(expirationTime)  // 만료 시간
                .signWith(secretKey, Jwts.SIG.HS256) // 서명에 필요한 비밀키, 서명 알고리즘
                .compact();
    }

    public void validateToken(String jwt) {
        try {
            parser(jwt);
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("기한이 지난 토큰입니다.");
        } catch (JwtException ex) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }

    public String getSubject(final String token) {
        return parser(token)
                .getPayload()
                .getSubject();
    }

    private Jws<Claims> parser(String jwt) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt);
    }
}
