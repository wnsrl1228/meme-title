package com.memetitle.meme.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.global.config.WebConfig;
import com.memetitle.meme.dto.request.TitleCreateRequest;
import com.memetitle.meme.service.TitleLikeService;
import com.memetitle.meme.service.TitleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;

@WebMvcTest(value = {TitleLikeController.class, WebConfig.class, JwtProvider.class})
class TitleLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    private TitleLikeService titleLikeService;
    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setting() {
        given(jwtProvider.validateToken(any())).willReturn(null);
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    @Test
    @DisplayName("제목 좋아요 요청에 성공한다.")
    void createLike_success() throws Exception {
        // given
        Long titleId = 1L;
        given(titleLikeService.saveLike(any(), any())).willReturn(null);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/titles/{titleId}/like", titleId)
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("제목 좋아요 취소 요청에 성공한다.")
    void deleteLike_success() throws Exception {
        // given
        Long titleId = 1L;
        doNothing().when(titleLikeService).deleteLike(any(), any());

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/titles/{titleId}/like", titleId)
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}