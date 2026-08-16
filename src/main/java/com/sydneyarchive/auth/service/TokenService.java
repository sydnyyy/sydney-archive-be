package com.sydneyarchive.auth.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.sydneyarchive.auth.dto.internal.JwtContext;
import com.sydneyarchive.auth.enums.GuestTokenFamilyStatus;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.exception.JwtAuthException;
import com.sydneyarchive.global.security.jwt.JwtProvider;
import com.sydneyarchive.user.dto.internal.UserAuthContext;
import com.sydneyarchive.user.enums.Role;
import com.sydneyarchive.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private static final String REFRESH_TOKEN_REDIS_KEY_PREFIX = "rt:";
    private static final String FAMILY_STATUS_KEY_PREFIX = "rt:family-status:";

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;
    private final UserService userService;

    public JwtContext issueAccessTokenAndRefreshTokenForGuest() {
        UserAuthContext userAuthContext = userService.saveGuest();

        String familyId = generateAndSaveFamilyId();
        String accessToken = jwtProvider.generateAccessToken(userAuthContext.userId(), Role.GUEST);
        String refreshToken = jwtProvider.generateRefreshToken(userAuthContext.userId(), Role.GUEST, familyId);
        saveRefreshTokenToRedis(userAuthContext.userId(), refreshToken);
        return JwtContext.of(accessToken, refreshToken);
    }

    public String issueAndSaveRefreshToken(String userId, Role role) {
        String familyId = generateAndSaveFamilyId();
        String refreshToken = jwtProvider.generateRefreshToken(userId, role, familyId);
        saveRefreshTokenToRedis(userId, refreshToken);
        return refreshToken;
    }

    public JwtContext issueAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new JwtAuthException(ErrorCode.REFRESH_TOKEN_MISSING);
        }

        try {
            String userId = jwtProvider.getSubject(refreshToken);
            Role role = jwtProvider.getClaimRole(refreshToken);
            String familyId = jwtProvider.getClaimFamilyId(refreshToken);

            GuestTokenFamilyStatus familyStatus
                    = GuestTokenFamilyStatus.fromValue(redisTemplate.opsForValue().get(FAMILY_STATUS_KEY_PREFIX + familyId));

            if (familyStatus == GuestTokenFamilyStatus.REVOKED) {
                throw new JwtAuthException(ErrorCode.TOKEN_FAMILY_REVOKED);
            }

            if (familyStatus == GuestTokenFamilyStatus.UNKNOWN) {
                throw new JwtAuthException(ErrorCode.TOKEN_FAMILY_INVALID);
            }

            String newAccessToken = jwtProvider.generateAccessToken(userId, role);
            String newRefreshToken = jwtProvider.generateRefreshToken(userId, role, familyId);
            saveRefreshTokenToRedis(userId, newRefreshToken);
            return JwtContext.of(newAccessToken, newRefreshToken);

        } catch (JwtAuthException e) {
            deleteRefreshTokenFromRedis(refreshToken);
            throw e;
        }
    }

    private String generateAndSaveFamilyId() {
        String familyId = NanoIdUtils.randomNanoId();
        redisTemplate.opsForValue().set(
                FAMILY_STATUS_KEY_PREFIX + familyId,
                GuestTokenFamilyStatus.ACTIVE.name()
        );

        return familyId;
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

    public void revokeFamily(String familyId) {
        redisTemplate
                .opsForValue()
                .set(
                        FAMILY_STATUS_KEY_PREFIX + familyId,
                        GuestTokenFamilyStatus.REVOKED.name(),
                        Duration.ofHours(1)
                );
    }

    public void logout(String refreshToken) {
        deleteRefreshTokenFromRedis(refreshToken);
    }

    private void deleteRefreshTokenFromRedis(String refreshToken) {
        try {
            String userId = jwtProvider.getSubject(refreshToken);
            String key = REFRESH_TOKEN_REDIS_KEY_PREFIX + userId;
            redisTemplate.delete(key);
        } catch (JwtAuthException e) {
            log.warn("Could not remove token from Redis during logout: {}", e.getMessage());
        }
    }
}
