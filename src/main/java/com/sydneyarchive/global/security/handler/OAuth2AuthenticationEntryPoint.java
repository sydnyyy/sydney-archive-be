package com.sydneyarchive.global.security.handler;

import com.sydneyarchive.global.exception.CustomAuthenticationException;
import com.sydneyarchive.global.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2AuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        if (authException instanceof CustomAuthenticationException customAuthenticationException) {
            errorCode = customAuthenticationException.getErrorCode();
        }

        log.error("[AuthenticationEntryPoint] Authentication failed. request={} {}, errorCode={}",
                request.getMethod(), request.getRequestURI(), errorCode.getCode()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
        {
            "code": "UNAUTHORIZED",
            "message": "Authentication failed",
            "status": 401
        }
        """);
    }
}
