package com.theforbiddenland.auth.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.theforbiddenland.global.cookie.CookieUtil;
import com.theforbiddenland.global.exception.ErrorCode;
import com.theforbiddenland.global.exception.JwtAuthException;
import com.theforbiddenland.global.security.jwt.JwtUtil;
import com.theforbiddenland.user.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String REFRESH_TOKEN_REDIS_KEY_PREFIX = "RT:";
    private static final String OAUTH_SUCCESS_SID_KEY_PREFIX = "OAUTH_SUCCESS_SID:";
    private static final long OAUTH_SUCCESS_SID_EXPIRATION_SEC = 60 * 3;

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final CookieUtil cookieUtil;

    public void issueRefreshTokenToCookie(String userId, Role role, HttpServletResponse response) {
        String refreshToken = jwtUtil.generateRefreshToken(userId, role);
        saveRefreshTokenToRedis(userId, refreshToken);
        cookieUtil.setRefreshTokenCookie(refreshToken, response);
    }

    private void saveRefreshTokenToRedis(String userId, String refreshToken) {
        String key = REFRESH_TOKEN_REDIS_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                JwtUtil.REFRESH_TOKEN_EXPIRATION_SEC,
                TimeUnit.SECONDS
        );
    }

    public String generateOAuthSuccessSid(String userId) {
        String oauthSuccessSid = NanoIdUtils.randomNanoId();
        String key = OAUTH_SUCCESS_SID_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(
                key,
                oauthSuccessSid,
                OAUTH_SUCCESS_SID_EXPIRATION_SEC,
                TimeUnit.SECONDS
        );

        return oauthSuccessSid;
    }

    public String issueAccessToken(
            String oauthSuccessSid,
            HttpServletRequest request
    ) {
        try {
            String refreshToken = cookieUtil.getRefreshToken(request);
            String userId = jwtUtil.getClaimUserId(refreshToken);
            Role role = jwtUtil.getClaimRole(refreshToken);

            validateOAuthSuccessSid(userId, oauthSuccessSid);

            return jwtUtil.generateAccessToken(userId, role);
        } catch (JwtAuthException e) {
            throw e;
        }
    }

    private void validateOAuthSuccessSid(String userId, String oauthSuccessSid) {
        String key = OAUTH_SUCCESS_SID_KEY_PREFIX + userId;
        String savedSid = redisTemplate.opsForValue().get(key);
        if (savedSid == null) {
            throw new JwtAuthException(ErrorCode.LOGIN_SESSION_ID_EXPIRED);
        }

        if (!Objects.equals(savedSid, oauthSuccessSid)) {
            throw new JwtAuthException(ErrorCode.ACCESS_DENIED);
        }
    }

}
