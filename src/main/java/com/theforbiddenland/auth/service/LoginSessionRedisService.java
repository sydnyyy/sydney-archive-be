package com.theforbiddenland.auth.service;

import com.theforbiddenland.global.config.auth.LoginSessionProperties;
import com.theforbiddenland.global.config.system.SystemProperties;
import com.theforbiddenland.global.exception.LoginSessionException;
import com.theforbiddenland.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginSessionRedisService {

    private static final String LOGIN_SESSION_KEY_PREFIX = "login:session:";
    private static final String LOGIN_STATE_KEY_PREFIX = "login:state:";

    private static final String LOGIN_SESSION_AUTH_CODE_FIELD_NAME = "authCode";
    private static final String LOGIN_SESSION_VERSION_FIELD_NAME = "version";
    private static final Integer LOGIN_SESSION_VERSION_DEFAULT = 0;
    private static final String LOGIN_SESSION_STATE_FIELD_NAME = "state";
    private static final String LOGIN_SESSION_USER_ID_FIELD_NAME = "userId";

    private final StringRedisTemplate redisTemplate;
    private final LoginSessionProperties loginSessionProperties;
    private final SystemProperties systemProperties;

    public void saveLoginSession(String sid, String authCode) {
        String key = getLoginSessionKey(sid);
        Map<String, String> map = Map.of(
                LOGIN_SESSION_AUTH_CODE_FIELD_NAME, authCode,
                LOGIN_SESSION_VERSION_FIELD_NAME, LOGIN_SESSION_VERSION_DEFAULT.toString()
        );

        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, Duration.ofSeconds(
                loginSessionProperties.getLoginSessionTimeoutSec()
                        + systemProperties.getBufferTimeSec()
        ));
    }

    public boolean isAvailableLoginSession(String sid) {
        String key = getLoginSessionKey(sid);

        String script = """
                if redis.call('exists', KEYS[1]) == 0 then
                    return 0
                end
                
                local state = redis.call('HGET', KEYS[1], ARGV[1])
                if not state or string.len(string.gsub(state, '^%s*(.-)%s*$', '%1')) == 0 then
                    return 1
                end
                
                return 0
                """;

        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key),
                LOGIN_SESSION_STATE_FIELD_NAME
        );

        return result == 1;
    }

    public boolean bindStateToLoginSession(String sid, String state) {
        String sessionKey = getLoginSessionKey(sid);
        String stateKey = getLoginStateKey(state);

        String script = """
                local currentState = redis.call('HGET', KEYS[1], ARGV[1])
                
                if not currentState then
                    redis.call('HSET', KEYS[1], ARGV[1], ARGV[2])
                    redis.call('SET', KEYS[2], ARGV[3])
                    return 1
                else
                    return 0
                end
                """;

        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Arrays.asList(sessionKey, stateKey),
                LOGIN_SESSION_STATE_FIELD_NAME,
                state,
                sid
        );

        return result == 1L;
    }

    public boolean assignUserIdToLoginSession(String state, String userId) {
        String stateKey = getLoginStateKey(state);

        String script = """
            local sid = redis.call('GET', KEYS[1])
            
            if sid then
                local sessionKey = ARGV[1] .. sid
                redis.call('HSET', sessionKey, ARGV[2], ARGV[3])
                return 1
            else
                return 0
            end
            """;

        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(stateKey),
                LOGIN_SESSION_KEY_PREFIX,
                LOGIN_SESSION_USER_ID_FIELD_NAME,
                userId
        );

        return result == 1L;
    }

    public String verifySessionAndGetUserId(String sid, int version, String authCode) {
        String key = getLoginSessionKey(sid);

        String script = """
            local current_version = redis.call('HGET', KEYS[1], ARGV[1]);
            local current_authcode = redis.call('HGET', KEYS[1], ARGV[3]);
        
            if not current_version or not current_authcode then
                return "SESSION_NOT_FOUND"
            end

            if tonumber(current_version) ~= tonumber(ARGV[2]) then
                return "VERSION_MISMATCH"
            end
        
            if current_authcode ~= ARGV[4] then
                return "AUTH_CODE_MISMATCH"
            end
        
            local user_id = redis.call('HGET', KEYS[1], ARGV[5])
        
            if not user_id then
                return "USER_ID_NOT_FOUND"
            end
        
            return user_id
            """;

        String result = redisTemplate.execute(
                new DefaultRedisScript<>(script, String.class),
                Collections.singletonList(key),
                LOGIN_SESSION_VERSION_FIELD_NAME,
                String.valueOf(version),
                LOGIN_SESSION_AUTH_CODE_FIELD_NAME,
                authCode,
                LOGIN_SESSION_USER_ID_FIELD_NAME
        );

        switch (result) {
            case "SESSION_NOT_FOUND" -> throw new LoginSessionException(ErrorCode.LOGIN_SESSION_EXPIRED);
            case "VERSION_MISMATCH" -> throw new LoginSessionException(ErrorCode.LOGIN_VERSION_MISMATCH);
            case "AUTH_CODE_MISMATCH" -> throw new LoginSessionException(ErrorCode.AUTH_CODE_MISMATCH);
            case "USER_ID_NOT_FOUND" -> throw new LoginSessionException(ErrorCode.USER_ID_MISSING);
        }

        return result;
    }

    private String getLoginSessionKey(String sid) {
        return LOGIN_SESSION_KEY_PREFIX + sid;
    }

    private String getLoginStateKey(String state) {
        return LOGIN_STATE_KEY_PREFIX + state;
    }
}
