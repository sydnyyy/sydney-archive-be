package com.sydneyarchive.auth.service;

import com.sydneyarchive.auth.enums.Platform;
import com.sydneyarchive.global.config.auth.LoginSessionProperties;
import com.sydneyarchive.global.config.system.SystemProperties;
import com.sydneyarchive.global.exception.LoginSessionException;
import com.sydneyarchive.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginSessionRedisService {

    private static final String LOGIN_SESSION_KEY_PREFIX = "login:session:";
    private static final String LOGIN_STATE_KEY_PREFIX = "login:state:";

    public static final String LOGIN_SESSION_AUTH_CODE_FIELD_NAME = "authCode";
    public static final String LOGIN_SESSION_VERSION_FIELD_NAME = "version";
    private static final Integer LOGIN_SESSION_VERSION_DEFAULT = 0;
    public static final String LOGIN_SESSION_STATE_FIELD_NAME = "state";
    public static final String LOGIN_SESSION_USER_ID_FIELD_NAME = "userId";
    public static final String LOGIN_SESSION_PLATFORM_FIELD_NAME = "platform";

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
                        + systemProperties.bufferTimeSec()
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

    public boolean bindStateAndPlatformToLoginSession(String sid, String state, Platform platform) {
        String sessionKey = getLoginSessionKey(sid);
        String stateKey = getLoginStateKey(state);

        String script = """
                if redis.call('exists', KEYS[1]) == 0 then
                    return 0
                end
                
                local currentState = redis.call('HGET', KEYS[1], ARGV[1])
                
                if not currentState then
                    redis.call('HSET', KEYS[1], ARGV[1], ARGV[2])
                    redis.call('SET', KEYS[2], ARGV[3])
                    redis.call('hset', KEYS[1], ARGV[4], ARGV[5])
                    redis.call('hincrby', KEYS[1], ARGV[6], 1)
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
                sid,
                LOGIN_SESSION_PLATFORM_FIELD_NAME,
                platform.toString(),
                LOGIN_SESSION_VERSION_FIELD_NAME
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
                redis.call('hincrby', sessionKey, ARGV[4], 1)
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
                userId,
                LOGIN_SESSION_VERSION_FIELD_NAME
        );

        return result == 1L;
    }

    public String verifySessionAndGetUserId(String sid, int version, String authCode) {
        String sessionKey = getLoginSessionKey(sid);

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
        
            redis.call('hincrby', KEYS[1], ARGV[1], 1)
            return user_id
            """;

        String result = redisTemplate.execute(
                new DefaultRedisScript<>(script, String.class),
                Collections.singletonList(sessionKey),
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

    public Long getLoginSessionVersion(String sid) {
        String key = getLoginSessionKey(sid);

        String script = """
                if redis.call('exists', KEYS[1]) == 0 then
                    return -1
                end
                
                local current_version = redis.call('hget', KEYS[1], ARGV[1])
                
                if not current_version then
                    redis.call('hset', KEYS[1], ARGV[1], ARGV[2])
                    return tonumber(ARGV[2])
                end
                
                current_version = tonumber(current_version)
                
                if current_version >= 1 then
                    return redis.call('hincrby', KEYS[1], ARGV[1], 1)
                else
                    return current_version;
                end
                """;

        return redisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key),
                LOGIN_SESSION_VERSION_FIELD_NAME,
                String.valueOf(LOGIN_SESSION_VERSION_DEFAULT)
        );
    }

    public Map<String, Object> getLoginSessionEntries(String state) {
        String sid = redisTemplate.opsForValue().get(getLoginStateKey(state));
        if (sid == null) {
            throw new LoginSessionException(ErrorCode.LOGIN_SESSION_EXPIRED);
        }

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(getLoginSessionKey(sid));
        if (entries.isEmpty()) {
            throw new LoginSessionException(ErrorCode.LOGIN_SESSION_EXPIRED);
        }

        Map<String, Object> result = entries.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> String.valueOf(entry.getKey()),
                        Map.Entry::getValue
                ));

        result.put("sid", sid);
        return result;
    }

    public void deleteLoingSession(String sid) {
        String sessionKey = getLoginSessionKey(sid);

        String script = """
                if redis.call('exists', KEYS[1]) == 0 then
                    return
                end
                
                local current_state = redis.call('HGET', KEYS[1], ARGV[1])
                if current_state then
                    local stateKey = ARGV[2] .. current_state
                    redis.call('del', stateKey)
                end
                
                redis.call('del', KEYS[1])
                """;

        redisTemplate.execute(
                new DefaultRedisScript<>(script),
                Collections.singletonList(sessionKey),
                LOGIN_SESSION_STATE_FIELD_NAME,
                LOGIN_STATE_KEY_PREFIX
        );
    }

    private String getLoginSessionKey(String sid) {
        return LOGIN_SESSION_KEY_PREFIX + sid;
    }

    private String getLoginStateKey(String state) {
        return LOGIN_STATE_KEY_PREFIX + state;
    }
}
