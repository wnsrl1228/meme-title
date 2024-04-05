package com.memetitle.comment.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.comment.service.CommentLikeService;
import com.memetitle.global.config.WebConfig;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@WebMvcTest(value = {CommentLikeController.class, WebConfig.class, JwtProvider.class})
class CommentLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    private CommentLikeService commentLikeService;
    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setting() {
        given(jwtProvider.validateToken(any())).willReturn(null);
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    @Test
    @DisplayName("댓글 좋아요 요청에 성공한다.")
    void createLike_success() throws Exception {
        // given
        Long commentId = 1L;
        given(commentLikeService.saveLike(any(), any())).willReturn(null);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/comments/{commentId}/like", commentId)
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("댓글 좋아요 취소 요청에 성공한다.")
    void deleteLike_success() throws Exception {
        // given
        Long commentId = 1L;
        doNothing().when(commentLikeService).deleteLike(any(), any());

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/{commentId}/like", commentId)
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}