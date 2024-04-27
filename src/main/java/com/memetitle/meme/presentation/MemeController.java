package com.memetitle.meme.presentation;

import com.memetitle.auth.Admin;
import com.memetitle.auth.dto.AdminMember;
import com.memetitle.image.dto.FileInfoResponse;
import com.memetitle.image.infrastructure.AwsS3Provider;
import com.memetitle.meme.dto.MemeElement;
import com.memetitle.meme.dto.response.MemesResponse;
import com.memetitle.meme.service.MemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
public class MemeController {

    private final MemeService memeService;
    private final AwsS3Provider awsS3Provider;

    // TODO: aws s3로 변경한 상태, 임시용으로 추후 삭제
    @PostMapping("/memes")
    public ResponseEntity<Void> createMeme(
            @Admin AdminMember adminMember,
            @RequestPart("file") final MultipartFile multipartFile
    ) {
        FileInfoResponse fileInfoResponse = awsS3Provider.upload(multipartFile, "memes");
        final Long memeId = memeService.saveMeme(fileInfoResponse.getImgUrl(), multipartFile.getOriginalFilename());
        return ResponseEntity.created(URI.create("/memes/" + memeId)).build();
    }

    @GetMapping("/memes")
    public ResponseEntity<MemesResponse> getMemes(
            @PageableDefault(sort = "startDate", direction = DESC) final Pageable pageable
    ) {
        return ResponseEntity.ok().body(memeService.getPageableMemes(pageable));
    }

    @GetMapping("/memes/{memeId}")
    public ResponseEntity<MemeElement> getMeme(
            @PathVariable final Long memeId
    ) {
        return ResponseEntity.ok().body(memeService.getMemeByMemeId(memeId));
    }
}
