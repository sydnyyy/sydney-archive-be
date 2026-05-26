package com.theforbiddenland.global.cookie;

import com.theforbiddenland.global.config.cookie.CookieProperties;
import com.theforbiddenland.global.exception.ErrorCode;
import com.theforbiddenland.global.exception.JwtAuthException;
import com.theforbiddenland.global.security.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "TFLRT";

    private final CookieProperties cookieProperties;

    public void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .path(cookieProperties.path())
                .maxAge(JwtUtil.REFRESH_TOKEN_EXPIRATION_SEC)
                .httpOnly(true)
                .secure(cookieProperties.secure())
                .sameSite(cookieProperties.sameSite())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new JwtAuthException(ErrorCode.COOKIE_NOT_FOUND);
        }

        return Arrays.stream(cookies)
                .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new JwtAuthException(ErrorCode.REFRESH_TOKEN_MISSING));
    }
}
