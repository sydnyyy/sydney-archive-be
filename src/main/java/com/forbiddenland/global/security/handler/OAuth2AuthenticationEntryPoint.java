package com.forbiddenland.global.security.handler;

import com.forbiddenland.global.exception.CustomAuthenticationException;
import com.forbiddenland.global.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2AuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

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

        log.error("[OAuth2AuthenticationEntryPoint] errorCode={}", errorCode.getCode());

        response.sendRedirect(frontendBaseUrl + "/error?code=" + errorCode.getCode().toLowerCase());
    }
}
