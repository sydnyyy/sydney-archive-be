package com.forbiddenland.global.security.handler;

import com.forbiddenland.global.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        String errorCode = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
        if (exception instanceof OAuth2AuthenticationException) {
            errorCode = ((OAuth2AuthenticationException) exception).getError().getErrorCode();
        }

        log.error("Authentication failed. type={}, code={}",
                exception.getClass().getSimpleName(), errorCode);

        response.sendRedirect(frontendBaseUrl + "/error?code=" + errorCode.toLowerCase());
    }
}
