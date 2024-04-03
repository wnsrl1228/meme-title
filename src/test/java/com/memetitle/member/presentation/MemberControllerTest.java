package com.memetitle.member.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.comment.dto.CommentElement;
import com.memetitle.comment.dto.response.CommentsResponse;
import com.memetitle.global.config.WebConfig;
import com.memetitle.member.dto.request.ProfileModifyRequest;
import com.memetitle.member.dto.response.MemberResponse;
import com.memetitle.member.dto.response.OtherProfileResponse;
import com.memetitle.member.dto.response.ProfileResponse;
import com.memetitle.member.service.MemberService;
import com.memetitle.meme.dto.TitleElement;
import com.memetitle.meme.dto.response.TitlesResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@WebMvcTest(value = {MemberController.class, WebConfig.class, JwtProvider.class})
class MemberControllerTest {

    private static final String SAMPLE_COMMENT = "댓글입니다.";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setting() {
        given(jwtProvider.validateToken(any())).willReturn(null);
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    @Test
    @DisplayName("멤버 프로필 정보 요청에 성공한다.")
    void getMemberProfile_success() throws Exception {
        // given
        ProfileResponse profileResponse = ProfileResponse.builder()
                .email("email")
                .nickname("nickname")
                .imgUrl("imgUrl")
                .score(10)
                .build();

        given(memberService.getProfile(any())).willReturn(profileResponse);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/member/profile")
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(profileResponse)));
    }

    @Test
    @DisplayName("다른 멤버 프로필 정보 요청에 성공한다.")
    void getOtherMemberProfile_success() throws Exception {
        // given
        Long memberId = 1L;
        OtherProfileResponse otherProfileResponse = OtherProfileResponse.builder()
                .nickname("nickname")
                .imgUrl("imgUrl")
                .score(10)
                .build();

        given(memberService.getOtherProfile(any())).willReturn(otherProfileResponse);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/member/profile/{memberId}", memberId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(otherProfileResponse)));
    }

    @Test
    @DisplayName("프로필 정보 수정에 성공한다.")
    void modifyProfile_success() throws Exception {
        // given
        ProfileModifyRequest profileModifyRequest = new ProfileModifyRequest("newNickname", "newImgUrl");
        doNothing().when(memberService).updateProfile(any(), any());

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.patch("/member/profile")
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileModifyRequest)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("멤버가 작성한 제목 목록 조회에 성공한다.")
    void getTitlesForMember_success() throws Exception {
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
                .build();

        List<TitleElement> titles = new ArrayList<>();
        titles.add(titleElement);

        TitlesResponse titlesResponse = TitlesResponse.builder()
                .titles(titles)
                .build();

        given(memberService.getTitlesByMemberId(any())).willReturn(titlesResponse);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/member/titles")
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(titlesResponse)));
    }

    @Test
    @DisplayName("멤버가 작성한 댓글 목록 조회에 성공한다.")
    void getCommentsForMember_success() throws Exception {
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
                .build();

        List<CommentElement> comments = new ArrayList<>();
        comments.add(commentElement);

        CommentsResponse commentsResponse = CommentsResponse.builder()
                .comments(comments)
                .build();

        given(memberService.getCommentsByMemberId(any())).willReturn(commentsResponse);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/member/comments")
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(commentsResponse)));
    }
}