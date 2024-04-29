package com.memetitle.global.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Slf4j
@Component
public class LogAspect {

    @Pointcut("execution(* com.memetitle..*Controller.*(..))")
    public void controller() {
    }

    @AfterReturning(pointcut = "controller()", returning = "responseEntity")
    public void afterReturning(ResponseEntity<?> responseEntity) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (request != null) {
            HttpStatus statusCode = responseEntity.getStatusCode();
            if (statusCode != null) {
                log.info("[Response sent: {} {} {}]", request.getMethod(), request.getRequestURI(), statusCode);
            } else {
                log.info("[Response sent: {} {}]", request.getMethod(), request.getRequestURI());
            }
        }
    }
}
