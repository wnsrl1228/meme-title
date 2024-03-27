package com.memetitle.auth.presentation;

import com.memetitle.auth.dto.LoginTokens;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.auth.service.LoginService;
import com.memetitle.global.config.WebConfig;
import com.memetitle.global.exception.AuthException;
import com.memetitle.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {LoginController.class, WebConfig.class, JwtProvider.class})
public class LoginControllerTest {

    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";
    @MockBean
    private LoginService loginService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("로그인에 성공한다.")
    void login_success() throws Exception {
        // given
        LoginTokens loginTokens = new LoginTokens(ACCESS_TOKEN, REFRESH_TOKEN);
        given(loginService.login("test_code")).willReturn(loginTokens);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/login/kakao")
                        .param("code", "test_code")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value(ACCESS_TOKEN))
                .andExpect(MockMvcResultMatchers.cookie().value("refresh-token", REFRESH_TOKEN));
    }

    @Test
    @DisplayName("로그인 요청시 유효한 인가 코드가 아닌 경우 예외가 발생한다.")
    void login_fail_INVALID_AUTHORIZATION_CODE() throws Exception {
        // given
        willThrow(new AuthException(ErrorCode.INVALID_AUTHORIZATION_CODE)).given(loginService).login(any());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/login/kakao")
                        .param("code", "test_code")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ErrorCode.INVALID_AUTHORIZATION_CODE.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ErrorCode.INVALID_AUTHORIZATION_CODE.getMessage()));
    }

    @Test
    @DisplayName("토큰 갱신에 성공한다.")
    void refreshAccessToken_success() throws Exception {
        // given
        given(loginService.renewAccessToken(REFRESH_TOKEN)).willReturn(ACCESS_TOKEN);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/token")
                        .cookie(new Cookie("refresh-token", REFRESH_TOKEN))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value(ACCESS_TOKEN));
    }

    @Test
    @DisplayName("토큰 갱신시 유효하지 않은 리프레쉬 토큰일 경우 예외가 발생한다.")
    void refreshAccessToken_fail_INVALID_TOKEN() throws Exception {
        // given
        willThrow(new AuthException(ErrorCode.INVALID_TOKEN)).given(loginService).renewAccessToken(any());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/token")
                        .cookie(new Cookie("refresh-token", REFRESH_TOKEN))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ErrorCode.INVALID_TOKEN.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ErrorCode.INVALID_TOKEN.getMessage()));
    }
}