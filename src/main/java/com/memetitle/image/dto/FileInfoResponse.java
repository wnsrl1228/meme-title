package com.memetitle.image.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileInfoResponse {

    private String imgUrl;

    public static FileInfoResponse of(String imgUrl) {
        return FileInfoResponse.builder()
                .imgUrl(imgUrl)
                .build();
    }
}