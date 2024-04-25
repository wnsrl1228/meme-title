package com.memetitle.image.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.global.config.WebConfig;
import com.memetitle.image.dto.FileInfoResponse;
import com.memetitle.image.infrastructure.AwsS3Provider;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@WebMvcTest(value = {AwsS3Controller.class, WebConfig.class, JwtProvider.class})
class AwsS3ControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private AwsS3Provider awsS3Provider;

    @BeforeEach
    void setting() {
        given(jwtProvider.validateToken(any())).willReturn(null);
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    @Test
    @DisplayName("이미지 업로드에 성공한다.")
    void upload_success() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "image-content".getBytes());
        FileInfoResponse fileInfoResponse = FileInfoResponse.of("imgUrl");
        given(awsS3Provider.upload(any(), any())).willReturn(fileInfoResponse);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/image")
                        .file(file)
                        .header(AUTHORIZATION, "Bearer access-token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(fileInfoResponse)));
    }
}