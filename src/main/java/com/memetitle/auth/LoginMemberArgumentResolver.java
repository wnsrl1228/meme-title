package com.memetitle.auth;

import com.memetitle.auth.dto.LoginMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberType = LoginMember.class.isAssignableFrom(parameter.getParameterType());

        // Login어노테이션이 존재하고, LoginMember 객체를 담는 경우에만 true
        return hasLoginAnnotation && hasMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        log.info("LoginMemberArgumentResolver");
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final Long memberId = (Long) request.getAttribute("memberId");

        return new LoginMember(memberId);
    }
}
