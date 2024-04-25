package com.memetitle.image.presentation;

import com.memetitle.image.dto.FileInfoResponse;
import com.memetitle.image.infrastructure.AwsS3Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class AwsS3Controller {

    private final AwsS3Provider awsS3Provider;

    @PostMapping("/image")
    public ResponseEntity<FileInfoResponse> upload(
            @RequestPart("file") final MultipartFile multipartFile
    ) {
        final FileInfoResponse fileInfoResponse = awsS3Provider.upload(multipartFile, "profile-image");
        return ResponseEntity.ok().body(fileInfoResponse);
    }
}
