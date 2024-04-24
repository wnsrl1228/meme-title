package com.memetitle.auth.service;

import com.memetitle.auth.domain.RefreshToken;
import com.memetitle.auth.dto.LoginTokens;
import com.memetitle.auth.dto.MemberInfo;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.auth.infrastructure.OauthProvider;
import com.memetitle.auth.repository.RefreshTokenRepository;
import com.memetitle.global.exception.AuthException;
import com.memetitle.member.domain.Member;
import com.memetitle.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.memetitle.global.exception.ErrorCode.*;

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

        try {
            jwtProvider.validateToken(refreshToken);
        } catch (AuthException e) {
            throw new AuthException(INVALID_REFRESH_TOKEN);
        }

        final RefreshToken findRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthException(INVALID_REFRESH_TOKEN));

        return jwtProvider.createAccessToken(findRefreshToken.getMemberId().toString());
    }

    private Member save(final MemberInfo memberInfo) {

        int tryCount = 0;
        while (tryCount < 5) {
            memberInfo.generateRandomizedNickname();
            if (!memberRepository.existsByNickname(memberInfo.getNickname())) {
                return memberRepository.save(new Member(
                        memberInfo.getSnsTokenId(),
                        memberInfo.getEmail(),
                        memberInfo.getNickname()
                ));
            }
            tryCount++;
        }
        throw new AuthException(SERVER_ERROR);
    }

    public void logout(final String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }
}
