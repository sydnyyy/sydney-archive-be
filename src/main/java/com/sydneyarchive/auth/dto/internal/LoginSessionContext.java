package com.sydneyarchive.auth.dto.internal;

import com.sydneyarchive.auth.enums.Platform;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.exception.LoginSessionException;
import lombok.Builder;

import java.util.Map;
import java.util.Optional;

import static com.sydneyarchive.auth.service.LoginSessionRedisService.*;

@Builder
public record LoginSessionContext(
        String qrCodeBase64,
        String sid,
        String secret,
        Integer version,
        String state,
        String userId,
        Platform platform,
        long expiredAt
) {

    public static LoginSessionContext of(Map<String, Object> entries) {
        String sid = entries.get("sid").toString();
        if (sid == null) throw new LoginSessionException(ErrorCode.SID_MISSING);

        return LoginSessionContext.builder()
                .sid(sid)
                .secret(string(entries, LOGIN_SESSION_SECRET_FIELD_NAME))
                .version(integer(entries, LOGIN_SESSION_VERSION_FIELD_NAME))
                .state(string(entries, LOGIN_SESSION_STATE_FIELD_NAME))
                .userId(string(entries, LOGIN_SESSION_USER_ID_FIELD_NAME))
                .platform(platform(entries, LOGIN_SESSION_PLATFORM_FIELD_NAME))
                .build();
    }

    private static String string(Map<String, Object> map, String key) {
        return Optional.ofNullable(map.get(key))
                .map(Object::toString)
                .orElse(null);
    }

    private static Integer integer(Map<String, Object> map, String key) {
        return Optional.ofNullable(string(map, key))
                .map(Integer::parseInt)
                .orElse(null);
    }

    private static Platform platform(Map<String, Object> map, String key) {
        return Optional.ofNullable(string(map, key))
                .map(Platform::fromString)
                .orElse(null);
    }
}