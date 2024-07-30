package com.memetitle.comment.repository;

import com.memetitle.comment.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentCustomRepository {

    Page<CommentDto> findByTitleId(Long titleId, Pageable pageable);

    Page<CommentDto> findByTitleId(Long memberId, Long titleId, Pageable pageable);

}
