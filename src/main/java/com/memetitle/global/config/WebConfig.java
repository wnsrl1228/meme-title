package com.memetitle.global.config;

import com.memetitle.auth.AdminMemberArgumentResolver;
import com.memetitle.auth.AuthHandlerInterceptor;
import com.memetitle.auth.LoginMemberArgumentResolver;
import com.memetitle.auth.infrastructure.JwtProvider;
import com.memetitle.global.common.interceptor.PathMatcherInterceptor;
import com.memetitle.global.common.interceptor.PathMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtProvider jwtProvider;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowCredentials(true) // 쿠키,세션 허용 여부
                .exposedHeaders(HttpHeaders.LOCATION); // 리다이렉션 허용 여부
    }

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
                .includePathPattern("/logout", PathMethod.DELETE)
                .includePathPattern("/memes", PathMethod.POST)
                .includePathPattern("/memes/{memeId}", PathMethod.POST)
                .includePathPattern("/member/profile", PathMethod.GET)
                .includePathPattern("/memes/{memeId}/titles/{titleId}", PathMethod.DELETE)
                .includePathPattern("/titles/{titleId}", PathMethod.DELETE)
                .includePathPattern("/titles/{titleId}/comments", PathMethod.POST)
                .includePathPattern("/comments/{commentId}", PathMethod.PATCH)
                .includePathPattern("/comments/{commentId}", PathMethod.DELETE)
                .includePathPattern("/member/titles", PathMethod.GET)
                .includePathPattern("/member/comments", PathMethod.GET)
                .includePathPattern("/titles/{titleId}/like", PathMethod.POST)
                .includePathPattern("/titles/{titleId}/like", PathMethod.DELETE)
                .includePathPattern("/comments/{commentId}/like", PathMethod.POST)
                .includePathPattern("/comments/{commentId}/like", PathMethod.DELETE)
                .includePathPattern("/image", PathMethod.POST);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(jwtProvider));
        resolvers.add(new AdminMemberArgumentResolver(jwtProvider));
    }
}
