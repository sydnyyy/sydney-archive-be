package com.theforbiddenland.global.cookie;

import com.theforbiddenland.global.config.cookie.CookieProperties;
import com.theforbiddenland.global.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieProvider {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "TFLRT";

    private final CookieProperties cookieProperties;

    public void setRefreshTokenCookie(String refreshToken, HttpServletResponse httpServletResponse) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .path(cookieProperties.path())
                .maxAge(JwtUtil.REFRESH_TOKEN_EXPIRATION_SEC)
                .httpOnly(true)
                .secure(cookieProperties.secure())
                .sameSite(cookieProperties.sameSite())
                .build();

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
