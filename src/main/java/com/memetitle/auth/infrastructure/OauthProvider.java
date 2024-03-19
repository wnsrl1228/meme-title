package com.memetitle.auth.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.memetitle.auth.dto.MemberInfo;
import com.memetitle.auth.dto.OauthToken;
import com.memetitle.auth.dto.OidcPublicKey;
import com.memetitle.auth.dto.OidcPublicKeys;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.PublicKey;
import java.util.Date;
import java.util.Optional;

@Component
public class OauthProvider {

    private static final String PROPERTIES_PATH = "${oauth2.provider.kakao.";
    private static final String ID_TOKEN_ISS = "https://kauth.kakao.com";

    private final JwtProvider jwtProvider;

    private RestTemplate restTemplate = new RestTemplate();
    protected final String clientId;
    protected final String clientSecret;
    protected final String redirectUri;
    protected final String tokenUri;
    protected final String oidcPublicKeyUrl;

    public OauthProvider(
            @Value(PROPERTIES_PATH + "client-id}") final String clientId,
            @Value(PROPERTIES_PATH + "client-secret}") final String clientSecret,
            @Value(PROPERTIES_PATH + "redirect-url}") final String redirectUri,
            @Value(PROPERTIES_PATH + "token-url}") final String tokenUri,
            @Value(PROPERTIES_PATH + "oidc-public-key-url}") final String oidcPublicKeyUrl,
            final JwtProvider jwtProvider
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
        this.oidcPublicKeyUrl = oidcPublicKeyUrl;
        this.jwtProvider = jwtProvider;
    }

    /**
     * kakao 서버에서 인가 코드를 응답한 상황
     *    1. kakao 서버에 토큰 요청
     *    2. 토큰에서 idToken값을 가져와 1차적으로 토큰을 검증 (idToken 또한 jwt)
     *    3. 2차로 idToken의 서명이 유효하진 검증
     *    4. idToken을 디코딩하여 유저 정보를 얻음
     */
    public MemberInfo getMemberInfo(final String code) {
        final String idToken = requestToken(code);

        validateIdToken(idToken);
        final Claims payload = validateSignatureOfIdToken(idToken);

        return new MemberInfo(
                payload.getSubject(),
                payload.get("email", String.class),
                payload.get("nickname", String.class)
        );
    }

    private String requestToken(final String code) {

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");
        final HttpEntity<MultiValueMap<String, String>> accessTokenRequestEntity = new HttpEntity<>(params, headers);


        // 토큰 api 요청
        final ResponseEntity<OauthToken> accessTokenResponse = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                accessTokenRequestEntity,
                OauthToken.class
        );

        return Optional.ofNullable(accessTokenResponse.getBody())
                .orElseThrow(() -> new RuntimeException("잘못된 요청입니다."))
                .getIdToken();
    }

    private void validateIdToken(final String idToken) {
        final JsonNode payload = jwtProvider.getPayload(idToken);

        final long currentTime = new Date().getTime() / 1000;
        final long expirationTime = payload.get("exp").asLong();
        final String iss = payload.get("iss").asText();
        final String aud = payload.get("aud").asText();

        if (!iss.equals(ID_TOKEN_ISS) || !aud.equals(clientId)) {
            throw new RuntimeException("유효하지 않은 토큰값입니다.");
        } else if (expirationTime <= currentTime ) {
            throw new RuntimeException("만료된 토큰값입니다.");
        }
    }

    private Claims validateSignatureOfIdToken(final String idToken) {

        // 1. 공개키 목록 조회 api 요청
        final ResponseEntity<OidcPublicKeys> oidcPublicKeyResponse = restTemplate.exchange(
                oidcPublicKeyUrl,
                HttpMethod.GET,
                null, OidcPublicKeys.class
        );

        if (oidcPublicKeyResponse.getStatusCode().isError()) {
            throw new RuntimeException("공개키 목록 조회에 실패하였습니다.");
        }

        // 2. 공개키 목록에서 헤더의 kid에 해당하는 공개키 값 확인
        final JsonNode header = jwtProvider.getHeader(idToken);
        final String kid = header.get("kid").asText();

        final OidcPublicKeys oidcPublickeys = oidcPublicKeyResponse.getBody();

        final OidcPublicKey oidcPublicKey = oidcPublickeys.getKeys().stream()
                .filter(key -> key.getKid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));


        // 3. 찾은 공개키의 modulus과 exponent으로 RSA 복호화 공개키 생성
        final PublicKey publicKey = jwtProvider.generateJwtKeyDecryption(oidcPublicKey.getModulus(), oidcPublicKey.getExponent());

        // 4. idToken의 서명 검증
        return jwtProvider.validateToken(idToken, publicKey).getPayload();
    }
}
