package com.memetitle.image.service;

import com.memetitle.global.exception.ErrorCode;
import com.memetitle.global.exception.StorageException;
import com.memetitle.image.infrastructure.AwsS3Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AwsS3ServiceTest {

    @Autowired
    private AwsS3Provider awsS3Service;

    private static final String IMAGE_PATH = "src/test/resources/image/";    // 이미지 저장 경로
    private static final String VALID_IMAGE_NAME = "valid-image.jpg";               // 유효한 이미지 이름
    private static final String INVALID_IMAGE_NAME = "invalid-image.jpg";              // 유효하지 않은 이미지 이름

    @DisplayName("확장자는 jpg이지만 유효하지 않은 파일일 경우 예외를 반환한다.")
    @Test
    void validateImage_invalidFile() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                INVALID_IMAGE_NAME,
                "image/jpg",
                new FileInputStream(IMAGE_PATH + INVALID_IMAGE_NAME));

        assertThatThrownBy(() -> awsS3Service.validateImage(multipartFile))
                .isInstanceOf(StorageException.class)
                .hasMessage(ErrorCode.INVALID_FILE.getMessage());
    }

    @DisplayName("이미지 확장자가 아닐 경우 InvalidImageExtensionException 예외를 반환한다.")
    @Test
    void validateImage_invalidExtension() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                VALID_IMAGE_NAME,
                "image/txt",
                new FileInputStream(IMAGE_PATH + VALID_IMAGE_NAME));

        assertThatThrownBy(() -> awsS3Service.validateImage(multipartFile))
                .isInstanceOf(StorageException.class)
                .hasMessage(ErrorCode.INVALID_FILE_EXTENSION.getMessage());
    }

}