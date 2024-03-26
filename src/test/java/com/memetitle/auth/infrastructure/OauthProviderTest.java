package com.memetitle.auth.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memetitle.auth.dto.MemberInfo;
import com.memetitle.auth.dto.OauthToken;
import com.memetitle.auth.dto.OidcPublicKey;
import com.memetitle.auth.dto.OidcPublicKeys;
import com.memetitle.global.config.RestTemplateConfig;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@RestClientTest(value = {OauthProvider.class, OauthCacheProvider.class, JwtProvider.class, RestTemplateConfig.class, RestTemplateBuilder.class})
class OauthProviderTest {

    private static final Long SAMPLE_EXPIRATION_TIME = 60000L;
    private static final Long SAMPLE_EXPIRED_TIME = 0L;
    private static final String SAMPLE_SUBJECT = "123456789";
    private static final String SAMPLE_KID = "2f252dada5f233f93d2f5528d12fea";
    private static final String INVALID_SAMPLE_KID = "2f252dadaaaaaaaaaf5528d12fea";
    private static final String SAMPLE_ISSUER = "https://kauth.kakao.com";
    private static final String INVALID_SAMPLE_ISSUER = "https://kauth.kokoa.com";
    private static final String INVALID_CLIENT_ID = "error";
    private static final String SAMPLE_MODULUS = "q8zZ0b_MNaLd6Ny8wd4cjFomilLfFIZcmhNSc1ttx_oQdJJZt5CDHB8WWwPGBUDUyY8AmfglS9Y1qA0_fxxs-ZUWdt45jSbUxghKNYgEwSutfM5sROh3srm5TiLW4YfOvKytGW1r9TQEdLe98ork8-rNRYPybRI3SKoqpci1m1QOcvUg4xEYRvbZIWku24DNMSeheytKUz6Ni4kKOVkzfGN11rUj1IrlRR-LNA9V9ZYmeoywy3k066rD5TaZHor5bM5gIzt1B4FmUuFITpXKGQZS5Hn_Ck8Bgc8kLWGAU8TzmOzLeROosqKE0eZJ4ESLMImTb2XSEZuN1wFyL0VtJw";
    private static final String SAMPLE_EXPONENT = "AQAB";
    private static final String SAMPLE_EMAIL = "hello123@naver.com";
    private static final String SAMPLE_NICKNAME = "hello";

    @MockBean
    private OauthCacheProvider oauthCacheProvider;

    @Autowired
    private OauthProvider oauthProvider;

    @Autowired
    private MockRestServiceServer mockServer;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${oauth2.provider.kakao.token-url}")
    private String tokenUrl;
    @Value("${oauth2.provider.kakao.client-id}")
    private String clientId;

    private String makeTestIdTokenJwt(
            final Long accessExpirationTime,
            final String kid,
            final String subject,
            final String aud,
            final String email,
            final String nickname,
            final String issuer,
            final PrivateKey privateKey
    ) {
        final Date now = new Date();
        final Date expirationTime = new Date(now.getTime() + accessExpirationTime);

        return Jwts.builder()
                .header().keyId(kid)
                .and()
                .subject(subject) // 토큰 제목
                .claim("aud", aud)
                .claim("email", email)
                .claim("nickname", nickname)
                .issuer(issuer)
                .issuedAt(now)  // 발급 시간
                .expiration(expirationTime)  // 만료 시간
                .signWith(privateKey) // 서명에 필요한 비밀키, 서명 알고리즘
                .compact();
    }

    @Test
    @DisplayName("oauth2 로 멤버 조회에 성공한다.")
    void getMemberInfo_success() throws JsonProcessingException {

        // given
        KeyPair keyPair = Jwts.SIG.RS256.keyPair().build();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
        String modulus = Base64.getUrlEncoder().withoutPadding().encodeToString(rsaPublicKey.getModulus().toByteArray());
        String exponent = Base64.getUrlEncoder().withoutPadding().encodeToString(rsaPublicKey.getPublicExponent().toByteArray());

        String idToken = makeTestIdTokenJwt(SAMPLE_EXPIRATION_TIME, SAMPLE_KID, SAMPLE_SUBJECT, clientId, SAMPLE_EMAIL, SAMPLE_NICKNAME, SAMPLE_ISSUER, privateKey );
        OauthToken oauthToken = getOauthTokenFixture(idToken);

        String expectResult = objectMapper.writeValueAsString(oauthToken);
        mockServer.expect(MockRestRequestMatchers.requestTo(tokenUrl))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(expectResult, MediaType.APPLICATION_JSON));


        OidcPublicKeys oidcPublicKeys = getOidcPublicKeysFixture(SAMPLE_KID, modulus, exponent);
        given(oauthCacheProvider.requestOidcPublicKeys()).willReturn(oidcPublicKeys);

        // when
        MemberInfo memberInfo = oauthProvider.getMemberInfo("code");

        // then
        assertThat(SAMPLE_EMAIL).isEqualTo(memberInfo.getEmail());
        assertThat(SAMPLE_NICKNAME).isEqualTo(memberInfo.getNickname());
        assertThat(SAMPLE_SUBJECT).isEqualTo(memberInfo.getSnsTokenId());
        mockServer.verify();
    }

    private OauthToken getOauthTokenFixture(String idToken) {
        return new OauthToken("", idToken, 0, "", "");
    }

    private OidcPublicKeys getOidcPublicKeysFixture(String kid, String modulus, String exponent) {
        ArrayList<OidcPublicKey> oidcPublicKeyList = new ArrayList<>();
        oidcPublicKeyList.add(new OidcPublicKey(kid, "", "", "", modulus, exponent));

        OidcPublicKeys oidcPublicKeys = new OidcPublicKeys(oidcPublicKeyList);
        return oidcPublicKeys;
    }
}