package com.memetitle.auth.service;

import com.memetitle.auth.dto.MemberInfo;
import com.memetitle.auth.infrastructure.OauthProvider;
import com.memetitle.mebmer.domain.Member;
import com.memetitle.mebmer.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final OauthProvider oauthProvider;
    private final MemberRepository memberRepository;
    public void login(final String code) {

        MemberInfo memberInfo = oauthProvider.getMemberInfo(code);

        Member member = memberRepository.findBySnsTokenId(memberInfo.getSnsTokenId())
                .orElseGet(() -> save(memberInfo));

        /**
         * TODO : 토큰 생성 후 토큰 반환
         */
    }

    private Member save(MemberInfo memberInfo) {
        return memberRepository.save(new Member(
                memberInfo.getSnsLoginId(),
                memberInfo.getSnsTokenId(),
                memberInfo.getNickname()
        ));
    }
}
