package com.theforbiddenland.auth.service;

import com.theforbiddenland.global.config.auth.AuthSessionProperties;
import com.theforbiddenland.global.config.system.SystemProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthSessionRedisService {

    private static final String AUTH_SESSION_KEY_PREFIX = "auth:session:";

    private static final String AUTH_SESSION_AUTH_CODE_FIELD_NAME = "authCode";
    private static final String AUTH_SESSION_VERSION_FIELD_NAME = "version";
    private static final Integer AUTH_SESSION_VERSION_DEFAULT = 0;

    private final StringRedisTemplate redisTemplate;
    private final AuthSessionProperties authSessionProperties;
    private final SystemProperties systemProperties;

    public void saveAuthSession(String sid, String authCode) {
        String key = getAuthSessionKey(sid);
        Map<String, String> map = Map.of(
                AUTH_SESSION_AUTH_CODE_FIELD_NAME, authCode,
                AUTH_SESSION_VERSION_FIELD_NAME, AUTH_SESSION_VERSION_DEFAULT.toString()
        );

        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, Duration.ofSeconds(
                authSessionProperties.getAuthSessionTimeoutSec()
                        + systemProperties.getBufferTimeSec()
        ));
    }

    private String getAuthSessionKey(String sid) {
        return AUTH_SESSION_KEY_PREFIX + sid;
    }
}
