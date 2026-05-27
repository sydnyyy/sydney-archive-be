package com.theforbiddenland.global.security.jwt;

import com.theforbiddenland.global.config.auth.JwtProperties;
import com.theforbiddenland.global.exception.ErrorCode;
import com.theforbiddenland.global.exception.JwtAuthException;
import com.theforbiddenland.global.exception.JwtCreationException;
import com.theforbiddenland.user.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtUtil {

    private static final long ACCESS_TOKEN_EXPIRATION_SEC = 60 * 15;
    public static final long REFRESH_TOKEN_EXPIRATION_SEC = 60 * 60 * 24 * 14;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";

    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_ROLE = "role";

    private final JwtProperties jwtProperties;

    public String generateAccessToken(String userId, Role role) {
        return generateToken(userId, role, ACCESS_TOKEN_EXPIRATION_SEC);
    }

    public String generateRefreshToken(String userId, Role role) {
        return generateToken(userId, role, REFRESH_TOKEN_EXPIRATION_SEC);
    }

    private String generateToken(String userId, Role role, long validityInSeconds) {
        if (userId == null || userId.isBlank()) {
            throw new JwtCreationException("userId is required");
        }

        if (role == null) {
            throw new JwtCreationException("role is required");
        }

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInSeconds * 1000);

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(userId)
                .issuedAt(now)
                .expiration(expiry)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_ROLE, role.name())
                .signWith(getSigningKey())
                .compact();
    }

    public String getClaimUserId(String token) {
        Claims claims = getClaims(token);
        String userId = claims.get(CLAIM_USER_ID, String.class);
        if (userId == null || userId.isBlank()) {
            throw new JwtAuthException(ErrorCode.JWT_CLAIM_MISSING);
        }
        return userId;
    }

    public Role getClaimRole(String token) {
        try {
            Claims claims = getClaims(token);
            return Role.valueOf(claims.get(CLAIM_ROLE, String.class));
        } catch (IllegalArgumentException e) {
            throw new JwtAuthException(ErrorCode.INVALID_ROLE_VALUE);
        }
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
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
