package com.memetitle.meme.presentation;

import com.memetitle.auth.Login;
import com.memetitle.auth.dto.LoginMember;
import com.memetitle.meme.dto.request.TitleCreateRequest;
import com.memetitle.meme.dto.response.TitlesResponse;
import com.memetitle.meme.service.TitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RequiredArgsConstructor
@RestController
public class TitleController {

    private final TitleService titleService;

    @PostMapping("/memes/{memeId}")
    public ResponseEntity<Void> createTitle(
            @PathVariable final Long memeId,
            @Valid @RequestBody final TitleCreateRequest titleCreateRequest,
            @Login final LoginMember loginMember

    ) {
        final Long titleId = titleService.saveTitle(loginMember.getMemberId(), memeId, titleCreateRequest);
        return ResponseEntity.created(URI.create("/memes/" + memeId + "/titles/" + titleId)).build();
    }

    @GetMapping("/memes/{memeId}/titles")
    public ResponseEntity<TitlesResponse> getTitlesForMeme(
            @PathVariable final Long memeId
    ) {
        return ResponseEntity.ok(titleService.getTitlesByMemeId(memeId));
    }

    @DeleteMapping("/memes/{memeId}/titles/{titleId}")
    public ResponseEntity<Void> deleteTitle(
            @PathVariable final Long memeId,
            @PathVariable final Long titleId,
            @Login final LoginMember loginMember
    ) {
        titleService.deleteTitle(loginMember.getMemberId(), memeId, titleId);
        return ResponseEntity.noContent().build();
    }
}
