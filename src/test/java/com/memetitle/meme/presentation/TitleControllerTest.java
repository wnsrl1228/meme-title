package com.memetitle.meme.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.global.config.WebConfig;
import com.memetitle.member.dto.response.MemberResponse;
import com.memetitle.meme.dto.TitleElement;
import com.memetitle.meme.dto.request.TitleCreateRequest;
import com.memetitle.meme.dto.response.TitleDetailResponse;
import com.memetitle.meme.dto.response.TitlesResponse;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;

@WebMvcTest(value = {TitleController.class, WebConfig.class, JwtProvider.class})
class TitleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    private TitleService titleService;
    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setting() {
        given(jwtProvider.validateToken(any())).willReturn(null);
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    @Test
    @DisplayName("밈 제목 생성 요청에 성공한다.")
    void createTitle_success() throws Exception {
        // given
        Long memeId = 1L;
        TitleCreateRequest titleCreateRequest = new TitleCreateRequest("Test Title");
        given(titleService.saveTitle(any(), any(), any())).willReturn(1L);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/memes/{memeId}", memeId)
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(titleCreateRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string(LOCATION, "/memes/1/titles/1"));
    }

    @Test
    @DisplayName("밈 제목 생성 요청에 제목이 유효하지 않을 경우 예외가 발생한다.")
    void createTitle_Invalid_title() throws Exception {
        // given
        Long memeId = 1L;
        TitleCreateRequest titleCreateRequest = new TitleCreateRequest("");
        given(titleService.saveTitle(any(), any(), any())).willReturn(1L);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/memes/{memeId}", memeId)
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(titleCreateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("제목을 입력해주세요."));
    }

    @Test
    @DisplayName("해당 밈 제목 목록 요청에 성공한다.")
    void getTitlesForMeme_success() throws Exception {
        // given
        Long memeId = 1L;
        MemberResponse memberResponse = MemberResponse.builder()
                .id(1L)
                .nickname("닉네임")
                .imgUrl("imgUrl").build();

        TitleElement titleElement = TitleElement.builder()
                .title("제목")
                .memeId(1L)
                .member(memberResponse)
                .likeCount(0)
                .build();

        List<TitleElement> titles = new ArrayList<>();
        titles.add(titleElement);

        TitlesResponse titlesResponse = TitlesResponse.builder()
                .titles(titles)
                .isLast(true)
                .build();

        given(titleService.getPageableTitlesByMemeId(any(), any())).willReturn(titlesResponse);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/memes/{memeId}/titles", memeId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(titlesResponse)));
    }

    @Test
    @DisplayName("밈 제목 상세 정보 요청에 성공한다.")
    void getTitleDetail_success() throws Exception {
        // given
        Long titleId = 1L;
        MemberResponse memberResponse = MemberResponse.builder()
                .id(1L)
                .nickname("닉네임")
                .imgUrl("imgUrl").build();

        TitleDetailResponse titleDetailResponse = TitleDetailResponse.builder()
                .title("제목")
                .memeId(1L)
                .member(memberResponse)
                .isOwner(true)
                .isLiked(false)
                .likeCount(0)
                .build();

        given(titleService.getTitleById(any(), any())).willReturn(titleDetailResponse);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/titles/{titleId}", titleId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(titleDetailResponse)));
    }

    @Test
    @DisplayName("밈 제목 삭제 요청에 성공한다.")
    void deleteTitle_success() throws Exception {
        // given
        Long titleId = 1L;
        doNothing().when(titleService).deleteTitle(any(), any());

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/titles/{titleId}", titleId)
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}