package com.memetitle.auth.infrastructure;

import com.memetitle.auth.dto.OidcPublicKeys;
import com.memetitle.global.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import static com.memetitle.global.exception.ErrorCode.FAILED_TO_RETRIEVE_PUBLIC_KEY_LIST;

/**
 * 동일 클래스에서 @Cacheable 메서드 동작 안되기 때문에 따로 분리 (AOP issue)
 */
@Slf4j
@Component
public class OauthCacheProvider {

    private static final String PROPERTIES_PATH = "${oauth2.provider.kakao.";
    private static final String PUBLIC_KEY_REQUEST_TTL = "2592000000"; // 30일
    private final RestTemplate restTemplate;
    protected final String oidcPublicKeyUrl;

    public OauthCacheProvider(
            @Value(PROPERTIES_PATH + "oidc-public-key-url}") final String oidcPublicKeyUrl,
            final RestTemplate restTemplate
    ) {
        this.oidcPublicKeyUrl = oidcPublicKeyUrl;
        this.restTemplate = restTemplate;
    }

    @Cacheable("oidcPublicKeysCache")
    public OidcPublicKeys requestOidcPublicKeys() {
        try {
            final ResponseEntity<OidcPublicKeys> oidcPublicKeyResponse = restTemplate.exchange(
                    oidcPublicKeyUrl,
                    HttpMethod.GET,
                    null, OidcPublicKeys.class
            );
            if (oidcPublicKeyResponse.getStatusCode().isError()) {
                throw new AuthException(FAILED_TO_RETRIEVE_PUBLIC_KEY_LIST);
            }
            return oidcPublicKeyResponse.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException | UnknownHttpStatusCodeException e) {
            throw new AuthException(FAILED_TO_RETRIEVE_PUBLIC_KEY_LIST);
        }
    }

    @CacheEvict(value = "oidcPublicKeysCache", allEntries = true)
    @Scheduled(fixedRateString = PUBLIC_KEY_REQUEST_TTL)
    public void clearOidcPublicKeysCache() {
        log.info("=== OIDC 공개키 목록 캐시 삭제 ===");
    }
}
