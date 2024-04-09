package com.memetitle.meme.presentation;

import com.memetitle.meme.dto.response.TopTitlesResponse;
import com.memetitle.meme.service.TopTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TopTitleController {

    private final TopTitleService topTitleService;

    @GetMapping("/memes/{memeId}/top")
    public ResponseEntity<TopTitlesResponse> getTopTitlesForMeme(
            @PathVariable final Long memeId
    ) {
        return ResponseEntity.ok(topTitleService.getTopTitlesByMemeId(memeId));
    }
}
