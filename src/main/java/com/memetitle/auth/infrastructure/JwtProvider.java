package com.memetitle.auth.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memetitle.auth.dto.LoginTokens;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final String issuer;
    private final Long accessExpirationTime;
    private final Long refreshExpirationTime;

    public JwtProvider(
            @Value("${jwt.secret-key}") final String secretKey,
            @Value("${jwt.claims.issuer}") final String issuer,
            @Value("${jwt.access-expiration-time}") final Long accessExpirationTime,
            @Value("${jwt.refresh-expiration-time}") final Long refreshExpirationTime
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.issuer = issuer;
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    public LoginTokens createLoginTokens(final String subject) {
        String accessToken = createToken(subject, accessExpirationTime);
        String refreshToken = createToken(subject, refreshExpirationTime);

        return new LoginTokens(accessToken, refreshToken);
    }

    public String createAccessToken(final String subject) {
        return createToken(subject, accessExpirationTime);
    }

    private String createToken(final String subject, final Long expiration) {
        final Date now = new Date();
        final Date expirationTime = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .issuer(issuer)  // 발급자
                .subject(subject) // 토큰 제목
                .issuedAt(now)  // 발급 시간
                .expiration(expirationTime)  // 만료 시간
                .signWith(secretKey, Jwts.SIG.HS256) // 서명에 필요한 비밀키, 서명 알고리즘
                .compact();
    }

    public Jws<Claims> validateToken(final String jwt) {
        return validateToken(jwt, null);
    }

    public Jws<Claims> validateToken(final String jwt, final PublicKey publicKey) {
        try {
            if (publicKey == null) {
                return parser(jwt);
            } else {
                return parser(jwt, publicKey);
            }
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

    private Jws<Claims> parser(final String jwt, final PublicKey publicKey) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(jwt);
    }

    private Jws<Claims> parser(final String jwt) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt);
    }

    // 공개키 생성 메서드
    public PublicKey generateJwtKeyDecryption(final String modulus, final String exponent) {
        final byte[] modulusBytes = Base64.getUrlDecoder().decode(modulus);
        final byte[] exponentBytes = Base64.getUrlDecoder().decode(exponent);

        final RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(
                new BigInteger(1, modulusBytes),
                new BigInteger(1, exponentBytes)
        );

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode getPayload(final String jwt) {
        return parseJson(decodeTokenPart(jwt, 1));
    }

    public JsonNode getHeader(final String jwt) {
        return parseJson(decodeTokenPart(jwt, 0));
    }

    private String decodeTokenPart(final String jwt, final int part) {
        final String[] splitToken = jwt.split("\\.");
        if (splitToken.length != 3)
            throw new RuntimeException("jwt 형식이 아닙니다.");

        return new String(Base64.getDecoder().decode(splitToken[part]));
    }

    private JsonNode parseJson(final String jsonString) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Cacheable("hello")
    public String hello() {
        try {
            Thread.sleep(1000 * 5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("hello 내부");
        return "hello";
    }
}
