package com.memetitle.meme.presentation;

import com.memetitle.meme.dto.response.MemesResponse;
import com.memetitle.meme.service.MemeService;
import com.memetitle.meme.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class MemeController {

    private final MemeService memeService;
    private final StorageService storageService;

    // TODO: 관리자 전용
    @PostMapping("/memes")
    public ResponseEntity<Void> createMeme(
            @RequestPart("file") final MultipartFile multipartFile
    ) {
        final String imgUrl = storageService.store(multipartFile);
        final Long memeId = memeService.saveMeme(imgUrl, multipartFile.getOriginalFilename());
        return ResponseEntity.created(URI.create("/memes/" + memeId)).build();
    }

    @GetMapping("/memes")
    public ResponseEntity<MemesResponse> getMemes() {
        return ResponseEntity.ok().body(memeService.getMemeAll());
    }
}
