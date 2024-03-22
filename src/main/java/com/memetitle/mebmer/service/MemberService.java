package com.memetitle.mebmer.service;

import com.memetitle.mebmer.domain.Member;
import com.memetitle.mebmer.dto.response.ProfileResponse;
import com.memetitle.mebmer.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));

        return ProfileResponse.of(member);
    }
}
