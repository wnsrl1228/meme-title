package com.memetitle.meme.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.global.config.WebConfig;
import com.memetitle.meme.dto.MemeElement;
import com.memetitle.meme.dto.response.MemesResponse;
import com.memetitle.meme.service.MemeService;
import com.memetitle.meme.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;

@WebMvcTest(value = {MemeController.class, WebConfig.class, JwtProvider.class})
class MemeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    private MemeService memeService;
    @MockBean
    private StorageService storageService;
    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setting() {
        given(jwtProvider.validateToken(any())).willReturn(null);
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    @Test
    @DisplayName("밈 생성 요청에 성공한다.")
    void createMeme() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());
        given(storageService.store(any())).willReturn(null);
        given(memeService.saveMeme(any(), any())).willReturn(1L);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/memes")
                        .file(file)
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string(LOCATION, "/memes/1"));
    }

    @Test
    @DisplayName("밈 조회 요청에 성공한다.")
    void getMemes() throws Exception {
        // given
        MemeElement memeElement = MemeElement.builder()
                .id(1L)
                .imgOriginalName("name")
                .imgUrl("/img")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .build();

        List<MemeElement> memeElements = new ArrayList<>();
        memeElements.add(memeElement);

        MemesResponse memesResponse = MemesResponse.builder()
                .memes(memeElements)
                .build();
        given(memeService.getMemeAll()).willReturn(memesResponse);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/memes")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(memesResponse)));
    }
}