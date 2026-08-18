package com.sydneyarchive.global.security.jwt;

import com.sydneyarchive.global.config.auth.JwtProperties;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.exception.JwtAuthException;
import com.sydneyarchive.user.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private static final long ACCESS_TOKEN_EXPIRATION_SEC = 60 * 15;
    public static final long REFRESH_TOKEN_EXPIRATION_SEC = 60 * 60 * 24 * 14;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_FAMILY_ID = "familyId";

    private final JwtProperties jwtProperties;

    public String generateAccessToken(String userId, Role role) {
        return generateToken(userId, role, ACCESS_TOKEN_EXPIRATION_SEC, null);
    }

    public String generateRefreshToken(String userId, Role role, String familyId) {
        if (familyId == null || familyId.isBlank()) {
            throw new JwtAuthException(ErrorCode.FAMILY_ID_REQUIRED);
        }

        return generateToken(userId, role, REFRESH_TOKEN_EXPIRATION_SEC, familyId);
    }

    private String generateToken(String userId, Role role, long validityInSeconds, String familyId) {
        if (userId == null || userId.isBlank()) {
            throw new JwtAuthException(ErrorCode.USER_ID_REQUIRED);
        }

        if (role == null) {
            throw new JwtAuthException(ErrorCode.ROLE_REQUIRED);
        }

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInSeconds * 1000);

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiry)
                .subject(userId)
                .claim(CLAIM_ROLE, role.name())
                .claim(CLAIM_FAMILY_ID, familyId)
                .signWith(getSigningKey())
                .compact();
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public Role getClaimRole(String token) {
        try {
            Claims claims = getClaims(token);
            String role = claims.get(CLAIM_ROLE, String.class);
            if (role == null || role.isBlank()) {
                throw new JwtAuthException(ErrorCode.CLAIM_ROLE_MISSING);
            }
            return Role.valueOf(claims.get(CLAIM_ROLE, String.class));
        } catch (IllegalArgumentException e) {
            throw new JwtAuthException(ErrorCode.INVALID_ROLE_VALUE);
        }
    }

    public String getClaimFamilyId(String token) {
        Claims claims = getClaims(token);
        String familyId = claims.get(CLAIM_FAMILY_ID, String.class);
        if (familyId == null || familyId.isBlank()) {
            throw new JwtAuthException(ErrorCode.CLAIM_FAMILY_ID_MISSING);
        }
        return familyId;
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired. message={}", e.getMessage());
            throw new JwtAuthException(ErrorCode.TOKEN_EXPIRED);

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT. message={}", e.getMessage());
            throw new JwtAuthException(ErrorCode.INVALID_TOKEN);
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(BEARER_TOKEN_PREFIX)) {
            return header.split(" ", 2)[1];
        }
        return null;
    }
}
