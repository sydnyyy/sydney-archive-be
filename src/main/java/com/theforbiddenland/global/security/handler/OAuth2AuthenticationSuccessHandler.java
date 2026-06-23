package com.theforbiddenland.global.security.handler;

import com.theforbiddenland.auth.dto.internal.CustomOAuth2User;
import com.theforbiddenland.auth.dto.internal.LoginSessionContext;
import com.theforbiddenland.auth.enums.Platform;
import com.theforbiddenland.auth.service.LoginSessionService;
import com.theforbiddenland.auth.service.TokenService;
import com.theforbiddenland.common.sse.enums.SseEventType;
import com.theforbiddenland.common.sse.service.SseService;
import com.theforbiddenland.global.exception.ErrorCode;
import com.theforbiddenland.global.exception.LoginSessionException;
import com.theforbiddenland.global.exception.UserException;
import com.theforbiddenland.user.dto.internal.UserAuthContext;
import com.theforbiddenland.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    private final UserService userService;
    private final LoginSessionService loginSessionService;
    private final TokenService tokenService;
    private final SseService sseService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        String state = request.getParameter("state");
        if (state == null) {
            log.warn("OAuth2 callback missing state parameter.");
            redirectToErrorPage(response, ErrorCode.LOGIN_PROCESSING_FAILED);
            return;
        }

        LoginSessionContext loginSessionContext;
        try {
            loginSessionContext = loginSessionService.getLoginSessionContext(state);
        } catch (LoginSessionException e) {
            redirectToErrorPage(response, ErrorCode.LOGIN_SESSION_EXPIRED);
            return;
        }

        CustomOAuth2User admin = (CustomOAuth2User) authentication.getPrincipal();
        UserAuthContext userAuthContext;
        try {
            userAuthContext = userService.saveAdmin(admin);
        } catch (UserException e) {
            log.error("Failed to save admin", e);
            redirectToErrorPage(response, ErrorCode.LOGIN_PROCESSING_FAILED);
            return;
        }

        boolean isAssigned = loginSessionService.assignUserIdToLoginSession(state, userAuthContext.userId());
        if (!isAssigned) {
            if (userAuthContext.created()) {
                userService.deleteAdmin(userAuthContext.userId());
            }
            redirectToErrorPage(response, ErrorCode.LOGIN_SESSION_EXPIRED);
            return;
        }

        String redirectUrl = frontendBaseUrl + "/login/success?platform=" + loginSessionContext.platform().toString().toLowerCase();
        response.sendRedirect(redirectUrl);

        if (loginSessionContext.platform() == Platform.WEB) {
            tokenService.issueRefreshTokenToCookie(
                    userAuthContext.userId(), userAuthContext.role(), response);
            return;
        }

        Long loginSessionVersion = loginSessionService.getLoginSessionVersion(loginSessionContext.sid());
        sseService.sendEvent(
                loginSessionContext.sid(),
                SseEventType.LOGIN_SUCCEEDED,
                Map.of(
                        "message", SseEventType.LOGIN_SUCCEEDED.getMessage(),
                        "version", loginSessionVersion
                )
        );
    }

    private void redirectToErrorPage(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.sendRedirect(frontendBaseUrl + "/error?code=" + errorCode.getCode().toLowerCase());
    }
}
