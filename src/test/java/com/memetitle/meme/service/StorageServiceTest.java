package com.memetitle.meme.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class StorageServiceTest {

    @Autowired
    private StorageService storageService;

    @Value("${storage.upload-dir}")
    private String location;

    @AfterEach
    void after() {
        storageService.deleteAll();
    }

    @Test
    @DisplayName("이미지 저장에 성공한다.")
    void store() {
        // given
        MultipartFile file = new MockMultipartFile(
                "file",                // 파일 이름
                "test.txt",            // 파일 이름
                "text/plain",          // 파일 타입
                "Hello, World!".getBytes(StandardCharsets.UTF_8) // 파일 내용
        );

        // when
        String imgUrl = storageService.store(file);

        // then
        Resource resource = storageService.loadAsResource(imgUrl);
        assertThat(resource.getFilename()).isEqualTo(file.getOriginalFilename());
    }
}