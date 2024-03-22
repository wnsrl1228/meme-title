package com.memetitle.auth.service;

import com.memetitle.auth.domain.RefreshToken;
import com.memetitle.auth.dto.LoginTokens;
import com.memetitle.auth.dto.MemberInfo;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.auth.infrastructure.OauthProvider;
import com.memetitle.auth.repository.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final OauthProvider oauthProvider;
    private final JwtProvider jwtProvider;

    public LoginTokens login(final String code) {

        final MemberInfo memberInfo = oauthProvider.getMemberInfo(code);

        final Member member = memberRepository.findBySnsTokenId(memberInfo.getSnsTokenId())
                .orElseGet(() -> save(memberInfo));

        final LoginTokens loginTokens = jwtProvider.createLoginTokens(member.getId().toString());

        refreshTokenRepository.findById(member.getId())
                .ifPresentOrElse(
                        refreshToken -> refreshToken.updateToken(loginTokens.getRefreshToken()),
                        () -> refreshTokenRepository.save(new RefreshToken(member.getId(), loginTokens.getRefreshToken()))
                );

        return loginTokens;
    }

    public String renewAccessToken(final String refreshToken) {

        jwtProvider.validateToken(refreshToken);

        final RefreshToken findRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("잘못된 토큰입니다."));

        return jwtProvider.createAccessToken(findRefreshToken.getMemberId().toString());
    }

    private Member save(final MemberInfo memberInfo) {
        return memberRepository.save(new Member(
                memberInfo.getSnsTokenId(),
                memberInfo.getEmail(),
                memberInfo.getNickname()
        ));
    }

}
