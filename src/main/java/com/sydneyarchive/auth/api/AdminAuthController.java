package com.sydneyarchive.auth.api;

import com.sydneyarchive.auth.dto.internal.JwtContext;
import com.sydneyarchive.auth.dto.request.AccessTokenIssueRequest;
import com.sydneyarchive.auth.service.LoginSessionService;
import com.sydneyarchive.auth.service.TokenService;
import com.sydneyarchive.global.cookie.CookieUtils;
import com.sydneyarchive.global.exception.CookieException;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.exception.JwtAuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.sydneyarchive.global.cookie.CookieUtils.REFRESH_TOKEN_COOKIE_NAME;

@RestController
@RequestMapping("/api/a/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final TokenService tokenService;
    private final LoginSessionService loginSessionService;
    private final CookieUtils cookieUtils;

    @PostMapping("/token/issue")
    public ResponseEntity<?> issueAccessToken(
            @RequestBody(required = false) AccessTokenIssueRequest accessTokenIssueRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (accessTokenIssueRequest != null && accessTokenIssueRequest.loginSessionSid() != null) {
            loginSessionService.terminateLoginSession(accessTokenIssueRequest.loginSessionSid());
        }

        try {
            String refreshToken = cookieUtils.getCookie(REFRESH_TOKEN_COOKIE_NAME, request);
            JwtContext jwtContext = tokenService.issueAccessToken(refreshToken);
            cookieUtils.setCookie(REFRESH_TOKEN_COOKIE_NAME, jwtContext.refreshToken(), response);
            return ResponseEntity.ok(Map.of("accessToken", jwtContext.accessToken()));
        } catch (CookieException e) {
            throw new JwtAuthException(ErrorCode.REFRESH_TOKEN_MISSING);
        } catch (JwtAuthException e) {
            cookieUtils.removeCookie(REFRESH_TOKEN_COOKIE_NAME, response);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = cookieUtils.getCookie(REFRESH_TOKEN_COOKIE_NAME, request);
        tokenService.logout(refreshToken);
        cookieUtils.removeCookie(REFRESH_TOKEN_COOKIE_NAME, response);
        return ResponseEntity.noContent().build();
    }
}
