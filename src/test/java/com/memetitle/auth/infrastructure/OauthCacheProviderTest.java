package com.memetitle.auth.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memetitle.auth.dto.OidcPublicKey;
import com.memetitle.auth.dto.OidcPublicKeys;
import com.memetitle.global.exception.AuthException;
import com.memetitle.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@RestClientTest(value = {OauthCacheProvider.class})
class OauthCacheProviderTest {

    @Autowired
    private OauthCacheProvider oauthCacheProvider;

    @Autowired
    private MockRestServiceServer mockServer;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${oauth2.provider.kakao.oidc-public-key-url}")
    private String oidcPublicKeyUrl;

    @Test
    @DisplayName("oidc 공개키 목록 조회에 성공한다.")
    void requestOidcPublicKeys_success() throws JsonProcessingException {

        // given
        OidcPublicKeys oidcPublicKeysFixture = getOidcPublicKeysFixture("1", "m", "e");

        String expectResult = objectMapper.writeValueAsString(oidcPublicKeysFixture);

        mockServer.expect(MockRestRequestMatchers.requestTo(oidcPublicKeyUrl))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(expectResult, MediaType.APPLICATION_JSON));

        // when
        OidcPublicKeys oidcPublicKeys = oauthCacheProvider.requestOidcPublicKeys();

        // then
        assertThat(1).isEqualTo(oidcPublicKeys.getKeys().size());
        assertThat("1").isEqualTo(oidcPublicKeys.getKeys().get(0).getKid());
        mockServer.verify();
    }
//
//    @Test
//    @DisplayName("oidc 공개키 목록 조회에 실패한다.")
//    void requestOidcPublicKeys_fail() throws JsonProcessingException {
//
//        // given
//        OidcPublicKeys oidcPublicKeysFixture = getOidcPublicKeysFixture("1", "m", "e");
//
//        String expectResult = objectMapper.writeValueAsString(oidcPublicKeysFixture);
//
//        mockServer.expect(MockRestRequestMatchers.requestTo(oidcPublicKeyUrl))
//                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
//                        .andRespond(MockRestResponseCreators.);
//
//        // when & then
//        assertThatThrownBy(() -> oauthCacheProvider.requestOidcPublicKeys())
//                .isInstanceOf(AuthException.class)
//                .hasMessage(ErrorCode.FAILED_TO_RETRIEVE_PUBLIC_KEY_LIST.getMessage());
//        mockServer.verify();
//    }

    private OidcPublicKeys getOidcPublicKeysFixture(String kid, String modulus, String exponent) {
        ArrayList<OidcPublicKey> oidcPublicKeyList = new ArrayList<>();
        oidcPublicKeyList.add(new OidcPublicKey(kid, "", "", "", modulus, exponent));

        OidcPublicKeys oidcPublicKeys = new OidcPublicKeys(oidcPublicKeyList);
        return oidcPublicKeys;
    }
}