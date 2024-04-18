package com.memetitle.auth.service;

import com.memetitle.auth.domain.RefreshToken;
import com.memetitle.auth.dto.LoginTokens;
import com.memetitle.auth.dto.MemberInfo;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.auth.infrastructure.OauthProvider;
import com.memetitle.auth.repository.RefreshTokenRepository;
import com.memetitle.global.exception.AuthException;
import com.memetitle.global.exception.ErrorCode;
import com.memetitle.member.domain.Member;
import com.memetitle.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
@Sql({"/h2-truncate.sql"})
class LoginServiceTest {

    private static final String SAMPLE_SNSTOKENID = "123";
    private static final String SAMPLE_EMAIL = "hello123@naver.com";
    private static final String SAMPLE_NICKNAME = "hello";

    @Autowired
    private LoginService loginService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private OauthProvider oauthProvider;

    @Test
    @DisplayName("최초 로그인에 성공한다.")
    void first_login_success() {
        // given
        given(oauthProvider.getMemberInfo("")).willReturn(new MemberInfo(SAMPLE_SNSTOKENID, SAMPLE_EMAIL, SAMPLE_NICKNAME));
        given(jwtProvider.createLoginTokens("1")).willReturn(new LoginTokens("accessToken", "refreshToken"));

        // when
        LoginTokens login = loginService.login("");

        // then
        Member member = memberRepository.findBySnsTokenId(SAMPLE_SNSTOKENID).get();
        RefreshToken refreshToken = refreshTokenRepository.findById(member.getId()).get();

        assertThat(1L).isEqualTo(member.getId());
        assertThat(SAMPLE_SNSTOKENID).isEqualTo(member.getSnsTokenId());
        assertThat(SAMPLE_EMAIL).isEqualTo(member.getEmail());
        assertThat(member.getNickname().startsWith(SAMPLE_NICKNAME)).isEqualTo(true);
        assertThat(refreshToken.getMemberId()).isEqualTo(member.getId());
        assertThat(refreshToken.getToken()).isEqualTo(login.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 시 리프레쉬 토큰을 업데이트 해준다.")
    void login_update_refreshToken() {
        // given
        given(oauthProvider.getMemberInfo("")).willReturn(new MemberInfo(SAMPLE_SNSTOKENID, SAMPLE_EMAIL, SAMPLE_NICKNAME));
        given(jwtProvider.createLoginTokens("1")).willReturn(new LoginTokens("accessToken", "beforeRefreshToken"));
        String beforeRefreshToken = loginService.login("").getRefreshToken();

        // when
        given(oauthProvider.getMemberInfo("")).willReturn(new MemberInfo(SAMPLE_SNSTOKENID, SAMPLE_EMAIL, SAMPLE_NICKNAME));
        given(jwtProvider.createLoginTokens("1")).willReturn(new LoginTokens("accessToken", "afterRefreshToken"));
        loginService.login("").getRefreshToken();

        // then
        Member member = memberRepository.findBySnsTokenId(SAMPLE_SNSTOKENID).get();
        RefreshToken afterRefreshToken = refreshTokenRepository.findById(member.getId()).get();
        assertThat(beforeRefreshToken).isNotEqualTo(afterRefreshToken.getToken());
    }

    @Test
    @DisplayName("토큰 갱신에 성공한다.")
    void renewAccessToken_success() {
        // given
        given(oauthProvider.getMemberInfo("")).willReturn(new MemberInfo(SAMPLE_SNSTOKENID, SAMPLE_EMAIL, SAMPLE_NICKNAME));
        given(jwtProvider.createLoginTokens("1")).willReturn(new LoginTokens("accessToken", "refreshToken"));
        String refreshToken = loginService.login("").getRefreshToken();

        // when
        given(jwtProvider.validateToken(refreshToken)).willReturn(null);
        given(jwtProvider.createAccessToken("1")).willReturn("accessToken");
        String accessToken = loginService.renewAccessToken(refreshToken);

        // then
        assertThat(accessToken).isEqualTo("accessToken");
    }

    @Test
    @DisplayName("토큰 갱신 시, DB에 존재하지 않는 리프레시 토큰을 사용할 경우 예외가 발생한다.")
    void renewAccessToken_INVALID_TOKEN() {

        // given
        given(jwtProvider.validateToken("")).willReturn(null);

        // when & then
        assertThatThrownBy(() -> loginService.renewAccessToken(""))
                .isInstanceOf(AuthException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("로그아웃에 성공한다.")
    void logout_success() {
        // given
        given(oauthProvider.getMemberInfo("")).willReturn(new MemberInfo(SAMPLE_SNSTOKENID, SAMPLE_EMAIL, SAMPLE_NICKNAME));
        given(jwtProvider.createLoginTokens("1")).willReturn(new LoginTokens("accessToken", "refreshToken"));
        LoginTokens login = loginService.login("");

        // when
        loginService.logout("refreshToken");

        // then
        assertThat(refreshTokenRepository.existsById(1L)).isEqualTo(false);
    }

}