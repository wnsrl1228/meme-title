package com.memetitle.comment.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.comment.dto.CommentElement;
import com.memetitle.comment.dto.request.CommentCreateRequest;
import com.memetitle.comment.dto.request.CommentModifyRequest;
import com.memetitle.comment.dto.response.CommentsResponse;
import com.memetitle.comment.service.CommentService;
import com.memetitle.global.config.WebConfig;
import com.memetitle.member.dto.response.MemberResponse;
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

@WebMvcTest(value = {CommentController.class, WebConfig.class, JwtProvider.class})
class CommentControllerTest {

    private static final String SAMPLE_COMMENT = "댓글입니다.";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setting() {
        given(jwtProvider.validateToken(any())).willReturn(null);
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    @Test
    @DisplayName("댓글 생성 요청에 성공한다.")
    void createComment_success() throws Exception {
        // given
        Long titleId = 1L;
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest(SAMPLE_COMMENT);
        given(commentService.saveComment(any(), any(), any())).willReturn(1L);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/titles/{titleId}/comments", titleId)
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string(LOCATION, "/titles/1/comments"));
    }

    @Test
    @DisplayName("해당 제목의 댓글 전체 조회에 성공한다.")
    void getCommentsForTitle() throws Exception {
        // given
        Long titleId = 1L;
        MemberResponse memberResponse = MemberResponse.builder()
                .id(1L)
                .nickname("닉네임")
                .imgUrl("imgUrl").build();

        CommentElement commentElement = CommentElement.builder()
                .id(1L)
                .titleId(1L)
                .content(SAMPLE_COMMENT)
                .member(memberResponse)
                .likeCount(1)
                .isOwner(true)
                .isLiked(false)
                .build();

        List<CommentElement> comments = new ArrayList<>();
        comments.add(commentElement);

        CommentsResponse commentsResponse = CommentsResponse.builder()
                .comments(comments)
                .isEmpty(false)
                .page(0)
                .totalElement(1L)
                .totalPages(1)
                .build();

        given(commentService.getPageableCommentsByTitleId(any(), any(), any())).willReturn(commentsResponse);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/titles/{titleId}/comments", titleId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(commentsResponse)));
    }

    @Test
    @DisplayName("댓글 수정에 성공한다.")
    void modifyComment() throws Exception {
        // given
        Long commentId = 1L;
        CommentModifyRequest commentModifyRequest = new CommentModifyRequest("변경된 댓글");
        doNothing().when(commentService).updateComment(any(), any(), any());

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.patch("/comments/{commentId}", commentId)
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentModifyRequest)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("댓글 삭제에 성공한다.")
    void deleteComment() throws Exception {
        // given
        Long commentId = 1L;
        doNothing().when(commentService).deleteComment(any(), any());

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/comments/{commentId}", commentId)
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}