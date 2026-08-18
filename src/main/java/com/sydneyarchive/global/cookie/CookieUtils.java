package com.sydneyarchive.global.cookie;

import com.sydneyarchive.global.config.auth.LoginSessionProperties;
import com.sydneyarchive.global.config.cookie.CookieProperties;
import com.sydneyarchive.global.exception.CookieException;
import com.sydneyarchive.global.exception.ErrorCode;
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
public class CookieUtils {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "SART";
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "OAR";

    private final CookieProperties cookieProperties;
    private final LoginSessionProperties loginSessionProperties;

    public void setCookie(String cookieName, String value, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, value)
                .path(cookieProperties.path())
                .maxAge(loginSessionProperties.getLoginSessionTimeoutSec())
                .httpOnly(true)
                .secure(cookieProperties.secure())
                .sameSite(cookieProperties.sameSite())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public String getCookie(String cookieName, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new CookieException(ErrorCode.COOKIE_NOT_FOUND);
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new CookieException(ErrorCode.COOKIE_NOT_FOUND));
    }

    public void removeCookie(String cookieName, HttpServletResponse httpServletResponse) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .path(cookieProperties.path())
                .maxAge(0)
                .httpOnly(true)
                .secure(cookieProperties.secure())
                .sameSite(cookieProperties.sameSite())
                .build();

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
