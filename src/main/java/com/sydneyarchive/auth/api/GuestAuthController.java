package com.sydneyarchive.auth.api;

import com.sydneyarchive.auth.dto.internal.JwtContext;
import com.sydneyarchive.auth.service.TokenService;
import com.sydneyarchive.global.cookie.CookieUtils;
import com.sydneyarchive.global.exception.CookieException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/g/auth")
@RequiredArgsConstructor
public class GuestAuthController {

    private final TokenService tokenService;
    private final CookieUtils cookieUtils;

    @PostMapping("/token/issue")
    private ResponseEntity<?> generateGuestToken(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {

        JwtContext jwtContext;
        try {
            String refreshToken = cookieUtils.getCookie(CookieUtils.REFRESH_TOKEN_COOKIE_NAME, httpServletRequest);
            jwtContext = tokenService.issueAccessToken(refreshToken);
        } catch (CookieException e) {
            jwtContext = tokenService.issueAccessTokenAndRefreshTokenForGuest();
        }

        cookieUtils.setCookie(CookieUtils.REFRESH_TOKEN_COOKIE_NAME, jwtContext.refreshToken(), httpServletResponse);
        return ResponseEntity.ok(Map.of("accessToken", jwtContext.accessToken()));
    }
}
