package com.sydneyarchive.auth.service;

import com.sydneyarchive.global.cookie.CookieUtils;
import com.sydneyarchive.global.exception.JwtAuthException;
import com.sydneyarchive.global.security.jwt.JwtProvider;
import com.sydneyarchive.user.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.sydneyarchive.global.cookie.CookieUtils.REFRESH_TOKEN_COOKIE_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private static final String REFRESH_TOKEN_REDIS_KEY_PREFIX = "RT:";

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;
    private final CookieUtils cookieUtils;

    public void issueRefreshTokenToCookie(String userId, Role role, HttpServletResponse response) {
        String refreshToken = jwtProvider.generateRefreshToken(userId, role);
        saveRefreshTokenToRedis(userId, refreshToken);
        cookieUtils.setCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken, response);
    }

    private void saveRefreshTokenToRedis(String userId, String refreshToken) {
        String key = REFRESH_TOKEN_REDIS_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                JwtProvider.REFRESH_TOKEN_EXPIRATION_SEC,
                TimeUnit.SECONDS
        );
    }

    public String issueAccessToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = null;
        try {
            refreshToken = cookieUtils.getCookie(REFRESH_TOKEN_COOKIE_NAME, request);
            String userId = jwtProvider.getClaimUserId(refreshToken);
            Role role = jwtProvider.getClaimRole(refreshToken);

            return jwtProvider.generateAccessToken(userId, role);
        } catch (JwtAuthException e) {
            if (refreshToken != null) {
                deleteRefreshTokenFromRedis(refreshToken);
            }
            cookieUtils.removeCookie(REFRESH_TOKEN_COOKIE_NAME, response);
            throw e;
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = cookieUtils.getCookie(REFRESH_TOKEN_COOKIE_NAME, request);
            deleteRefreshTokenFromRedis(refreshToken);
        } catch (JwtAuthException e) {
            log.info("Logout requested for invalid or missing token: {}", e.getMessage());
        } finally {
            cookieUtils.removeCookie(REFRESH_TOKEN_COOKIE_NAME, response);
        }

    }

    private void deleteRefreshTokenFromRedis(String refreshToken) {
        try {
            String userId = jwtProvider.getClaimUserId(refreshToken);
            String key = REFRESH_TOKEN_REDIS_KEY_PREFIX + userId;
            redisTemplate.delete(key);
        } catch (JwtAuthException e) {
            log.warn("Could not remove token from Redis during logout: {}", e.getMessage());
        }
    }
}
