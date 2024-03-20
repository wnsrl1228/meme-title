package com.memetitle.auth.infrastructure;

import com.memetitle.auth.dto.OidcPublicKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 동일 클래스에서 @Cacheable 메서드 동작 안되기 때문에 따로 분리 (AOP issue)
 */
@Slf4j
@Component
public class OauthCacheProvider {

    private static final String PUBLIC_KEY_REQUEST_TTL = "2592000000"; // 30일
    private static final String OIDC_PUBLIC_KEY_URL = "https://kauth.kakao.com/.well-known/jwks.json";
    private final RestTemplate restTemplate = new RestTemplate();

    @Cacheable("oidcPublicKeysCache")
    public OidcPublicKeys requestOidcPublicKeys() {
        final ResponseEntity<OidcPublicKeys> oidcPublicKeyResponse = restTemplate.exchange(
                OIDC_PUBLIC_KEY_URL,
                HttpMethod.GET,
                null, OidcPublicKeys.class
        );

        if (oidcPublicKeyResponse.getStatusCode().isError()) {
            throw new RuntimeException("공개키 목록 조회에 실패하였습니다.");
        }
        return oidcPublicKeyResponse.getBody();
    }

    @CacheEvict(value = "oidcPublicKeysCache", allEntries = true)
    @Scheduled(fixedRateString = PUBLIC_KEY_REQUEST_TTL)
    public void clearOidcPublicKeysCache() {
        log.info("=== OIDC 공개키 목록 캐시 삭제 ===");
    }
}
