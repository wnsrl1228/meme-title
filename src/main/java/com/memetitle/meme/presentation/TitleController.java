package com.memetitle.meme.presentation;

import com.memetitle.auth.Login;
import com.memetitle.auth.dto.LoginMember;
import com.memetitle.meme.dto.request.TitleCreateRequest;
import com.memetitle.meme.dto.response.TitleDetailResponse;
import com.memetitle.meme.dto.response.TitlesResponse;
import com.memetitle.meme.service.TitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.data.domain.Sort.Direction.DESC;

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
            @PathVariable final Long memeId,
            @PageableDefault(sort = "createdAt", direction = DESC) final Pageable pageable
    ) {
        return ResponseEntity.ok(titleService.getPageableTitlesByMemeId(memeId, pageable));
    }

    @GetMapping("/titles/{titleId}")
    public ResponseEntity<TitleDetailResponse> getTitleDetail(
            @PathVariable final Long titleId
    ) {
        return ResponseEntity.ok(titleService.getTitleById(titleId));
    }

    @DeleteMapping("/titles/{titleId}")
    public ResponseEntity<Void> deleteTitle(
            @PathVariable final Long titleId,
            @Login final LoginMember loginMember
    ) {
        titleService.deleteTitle(loginMember.getMemberId(), titleId);
        return ResponseEntity.noContent().build();
    }
}
