package com.memetitle.auth.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.memetitle.auth.dto.MemberInfo;
import com.memetitle.auth.dto.OauthToken;
import com.memetitle.auth.dto.OidcPublicKey;
import com.memetitle.auth.dto.OidcPublicKeys;
import com.memetitle.global.exception.AuthException;
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

import static com.memetitle.global.exception.ErrorCode.*;

@Component
public class OauthProvider {

    private static final String PROPERTIES_PATH = "${oauth2.provider.kakao.";
    private static final String ID_TOKEN_ISS = "https://kauth.kakao.com";
    private final JwtProvider jwtProvider;
    private final OauthCacheProvider oauthCacheProvider;
    private final RestTemplate restTemplate;
    protected final String clientId;
    protected final String clientSecret;
    protected final String redirectUrl;
    protected final String tokenUrl;

    public OauthProvider(
            @Value(PROPERTIES_PATH + "client-id}") final String clientId,
            @Value(PROPERTIES_PATH + "client-secret}") final String clientSecret,
            @Value(PROPERTIES_PATH + "redirect-url}") final String redirectUrl,
            @Value(PROPERTIES_PATH + "token-url}") final String tokenUrl,
            final OauthCacheProvider oauthCacheProvider,
            final JwtProvider jwtProvider,
            final RestTemplate restTemplate
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
        this.tokenUrl = tokenUrl;
        this.oauthCacheProvider = oauthCacheProvider;
        this.jwtProvider = jwtProvider;
        this.restTemplate = restTemplate;
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
        params.add("redirect_uri", redirectUrl);
        params.add("grant_type", "authorization_code");
        final HttpEntity<MultiValueMap<String, String>> accessTokenRequestEntity = new HttpEntity<>(params, headers);


        // 토큰 api 요청
        final ResponseEntity<OauthToken> accessTokenResponse = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                accessTokenRequestEntity,
                OauthToken.class
        );

        return Optional.ofNullable(accessTokenResponse.getBody())
                .orElseThrow(() -> new AuthException(INVALID_AUTHORIZATION_CODE))
                .getIdToken();
    }

    private void validateIdToken(final String idToken) {
        final JsonNode payload = jwtProvider.getPayload(idToken);

        final long currentTime = new Date().getTime() / 1000;
        final long expirationTime = payload.get("exp").asLong();
        final String iss = Optional.ofNullable(payload.get("iss")).map(JsonNode::asText)
                .orElseThrow(() -> new AuthException(INVALID_ID_TOKEN));
        final String aud = Optional.ofNullable(payload.get("aud")).map(JsonNode::asText)
                .orElseThrow(() -> new AuthException(INVALID_ID_TOKEN));

        if (!iss.equals(ID_TOKEN_ISS) || !aud.equals(clientId)) {
            throw new AuthException(INVALID_ID_TOKEN);
        } else if (expirationTime <= currentTime ) {
            throw new AuthException(EXPIRED_ID_TOKEN);
        }
    }

    private Claims validateSignatureOfIdToken(final String idToken) {

        // 1. 공개키 목록 조회 api 요청
        OidcPublicKeys oidcPublicKeys = oauthCacheProvider.requestOidcPublicKeys();

        // 2. 공개키 목록에서 헤더의 kid에 해당하는 공개키 값 확인
        final JsonNode header = jwtProvider.getHeader(idToken);
        final String kid = Optional.ofNullable(header.get("kid")).map(JsonNode::asText)
                .orElseThrow(() -> new AuthException(INVALID_ID_TOKEN));

        OidcPublicKey oidcPublicKey = findOidcPublicKey(oidcPublicKeys, kid);

        // 2-1. 공개키가 없는 경우
        if (oidcPublicKey == null) {
            // 2-2. 캐시 초기화 후 api 공개키 목록 재요청
            oauthCacheProvider.clearOidcPublicKeysCache();
            oidcPublicKeys = oauthCacheProvider.requestOidcPublicKeys();
            oidcPublicKey = findOidcPublicKey(oidcPublicKeys, kid);

            // 2-3. 재요청에도 공개키가 없는 경우 예외 발생
            if (oidcPublicKey == null)
                throw new AuthException(INVALID_ID_TOKEN);
        }

        // 3. 찾은 공개키의 modulus과 exponent으로 RSA 복호화 공개키 생성
        final PublicKey publicKey = jwtProvider.generateJwtKeyDecryption(oidcPublicKey.getModulus(), oidcPublicKey.getExponent());

        // 4. idToken의 서명 검증
        return jwtProvider.validateToken(idToken, publicKey).getPayload();
    }

    private OidcPublicKey findOidcPublicKey(OidcPublicKeys oidcPublicKeys, String kid) {
        return oidcPublicKeys.getKeys().stream()
                .filter(key -> key.getKid().equals(kid))
                .findFirst()
                .orElse(null);
    }

}
