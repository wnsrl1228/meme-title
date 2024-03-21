package com.memetitle.auth.service;

import com.memetitle.auth.dto.LoginToken;
import com.memetitle.auth.dto.MemberInfo;
import com.memetitle.auth.infrastructure.JwtProvider;
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

    private final MemberRepository memberRepository;
    private final OauthProvider oauthProvider;
    private final JwtProvider jwtProvider;

    public LoginToken login(final String code) {

        final MemberInfo memberInfo = oauthProvider.getMemberInfo(code);

        final Member member = memberRepository.findBySnsTokenId(memberInfo.getSnsTokenId())
                .orElseGet(() -> save(memberInfo));

        // TODO : 추후 엑세스 토큰 발급 후 반환
        String token = jwtProvider.createToken(member.getId().toString());
        return new LoginToken(token);
    }

    private Member save(MemberInfo memberInfo) {
        return memberRepository.save(new Member(
                memberInfo.getSnsTokenId(),
                memberInfo.getEmail(),
                memberInfo.getNickname()
        ));
    }
}
