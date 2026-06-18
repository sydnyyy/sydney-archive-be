package com.theforbiddenland.global.security.handler;

import com.theforbiddenland.auth.dto.internal.CustomOAuth2User;
import com.theforbiddenland.auth.service.AuthSessionService;
import com.theforbiddenland.auth.service.TokenService;
import com.theforbiddenland.global.exception.ErrorCode;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    private final UserService userService;
    private final AuthSessionService authSessionService;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        String state = request.getParameter("state");
        if (state == null) {
            log.warn("OAuth2 callback missing state parameter.");
            redirectToErrorPage(response, ErrorCode.AUTH_PROCESSING_FAILED);
            return;
        }

        CustomOAuth2User admin = (CustomOAuth2User) authentication.getPrincipal();
        UserAuthContext userAuthContext = userService.saveAdmin(admin);
        boolean isAssigned = authSessionService.assignUserIdToAuthSession(state, userAuthContext.userId());
        if (!isAssigned) {
            redirectToErrorPage(response, ErrorCode.AUTH_PROCESSING_FAILED);
            return;
        }

        tokenService.issueRefreshTokenToCookie(
                userAuthContext.userId(), userAuthContext.role(), response);

        String oauthSuccessSid = tokenService.generateOAuthSuccessSid(userAuthContext.userId());
        String redirectUrl = frontendBaseUrl + "/admin/oauth/success?sid=" + oauthSuccessSid;
        response.sendRedirect(redirectUrl);
    }

    private void redirectToErrorPage(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.sendRedirect(frontendBaseUrl + "/error?code=" + errorCode.getCode().toLowerCase());
    }
}
