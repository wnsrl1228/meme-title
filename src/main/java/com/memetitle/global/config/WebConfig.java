package com.memetitle.global.config;

import com.memetitle.auth.AuthHandlerInterceptor;
import com.memetitle.auth.LoginMemberArgumentResolver;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.global.common.interceptor.PathMatcherInterceptor;
import com.memetitle.global.common.interceptor.PathMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtProvider jwtProvider;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/login/kakao",
                        "/auth/token",
                        "/*.ico",
                        "/error"
                ); // 핸들러가 실행되면 안되는 애들
    }

    private HandlerInterceptor authHandlerInterceptor() {

        final PathMatcherInterceptor interceptor = new PathMatcherInterceptor(new AuthHandlerInterceptor(jwtProvider));

        return interceptor
                .includePathPattern("/memes", PathMethod.POST)
                .includePathPattern("/memes/{memeId}", PathMethod.POST)
                .includePathPattern("/member/profile", PathMethod.GET)
                .includePathPattern("/memes/{memeId}/titles/{titleId}", PathMethod.DELETE);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }
}
