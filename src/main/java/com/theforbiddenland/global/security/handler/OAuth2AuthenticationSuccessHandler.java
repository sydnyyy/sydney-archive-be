package com.theforbiddenland.global.security.handler;

import com.theforbiddenland.auth.dto.internal.CustomOAuth2User;
import com.theforbiddenland.auth.service.TokenService;
import com.theforbiddenland.user.dto.internal.UserAuthContext;
import com.theforbiddenland.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    private final UserService userService;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        CustomOAuth2User adminUser = (CustomOAuth2User) authentication.getPrincipal();
        UserAuthContext userAuthContext = userService.saveAdmin(adminUser);

        tokenService.issueRefreshTokenToCookie(
                userAuthContext.userId(), userAuthContext.role(), response);

        String oauthSuccessSid = tokenService.generateOAuthSuccessSid(userAuthContext.userId());
        String redirectUrl = frontendBaseUrl + "/admin/oauth/success?sid=" + oauthSuccessSid;
        response.sendRedirect(redirectUrl);
    }
}
