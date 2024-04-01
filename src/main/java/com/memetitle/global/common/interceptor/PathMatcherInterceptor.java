package com.memetitle.global.common.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PathMatcherInterceptor implements HandlerInterceptor {
    private final HandlerInterceptor handlerInterceptor;
    private final PathContainer pathContainer;

    public PathMatcherInterceptor(final HandlerInterceptor handlerInterceptor) {
        this.handlerInterceptor = handlerInterceptor;
        this.pathContainer = new PathContainer();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        if (pathContainer.notIncludedPath(request.getServletPath(), request.getMethod())) {
            return true;
        }

        return handlerInterceptor.preHandle(request, response, handler);
    }

    public PathMatcherInterceptor includePathPattern(String pathPattern, PathMethod pathMethod) {
        pathContainer.includePathPattern(pathPattern, pathMethod);
        return this;
    }

    public PathMatcherInterceptor excludePathPattern(String pathPattern, PathMethod pathMethod) {
        pathContainer.excludePathPattern(pathPattern, pathMethod);
        return this;
    }
}
