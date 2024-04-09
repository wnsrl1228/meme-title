package com.memetitle.meme.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.global.config.WebConfig;
import com.memetitle.member.dto.response.MemberResponse;
import com.memetitle.meme.dto.TitleElement;
import com.memetitle.meme.dto.response.TopTitlesResponse;
import com.memetitle.meme.service.TopTitleService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(value = {TopTitleController.class, WebConfig.class, JwtProvider.class})
class TopTitleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    private TopTitleService topTitleService;

    @Test
    @DisplayName("해당 meme의 인기 많은 3개의 제목 목록 요청에 성공한다.")
    void getTopTitlesForMeme_success() throws Exception {
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
                .likeCount(10)
                .build();

        List<TitleElement> titles = new ArrayList<>();
        titles.add(titleElement);

        TopTitlesResponse topTitlesResponse = TopTitlesResponse.builder()
                .titles(titles)
                .isEmpty(false)
                .build();

        given(topTitleService.getTopTitlesByMemeId(any())).willReturn(topTitlesResponse);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/memes/{memeId}/top", memeId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(topTitlesResponse)));
    }
}