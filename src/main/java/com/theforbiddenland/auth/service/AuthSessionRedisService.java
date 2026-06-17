package com.theforbiddenland.auth.service;

import com.theforbiddenland.global.config.auth.AuthSessionProperties;
import com.theforbiddenland.global.config.system.SystemProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthSessionRedisService {

    private static final String AUTH_SESSION_KEY_PREFIX = "auth:session:";

    private static final String AUTH_SESSION_AUTH_CODE_FIELD_NAME = "authCode";
    private static final String AUTH_SESSION_VERSION_FIELD_NAME = "version";
    private static final Integer AUTH_SESSION_VERSION_DEFAULT = 0;
    private static final String AUTH_SESSION_STATE_FIELD_NAME = "state";

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

    public boolean assignStateToAuthSession(String sid, String state) {
        String key = getAuthSessionKey(sid);

        String script = """
                local currentState = redis.call('HGET', KEYS[1], ARGV[1])
                
                if not currentState then
                    redis.call('HSET', KEYS[1], ARGV[1], ARGV[2])
                    return 1
                else
                    return 0
                end
                """;

        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key),
                AUTH_SESSION_STATE_FIELD_NAME,
                state
        );

        return result == 1L;
    }

    private String getAuthSessionKey(String sid) {
        return AUTH_SESSION_KEY_PREFIX + sid;
    }
}
