package com.theforbiddenland.global.security.jwt;

import com.theforbiddenland.global.config.auth.JwtProperties;
import com.theforbiddenland.global.exception.JwtCreationException;
import com.theforbiddenland.user.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtUtil {

    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 2;
    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 14;

    private final JwtProperties jwtProperties;

    public String generateAccessToken(String userId, Role role) {
        return generateToken(userId, role, ACCESS_TOKEN_VALIDITY_SECONDS);
    }

    public String generateRefreshToken(String userId, Role role) {
        return generateToken(userId, role, REFRESH_TOKEN_VALIDITY_SECONDS);
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
                .claim("userId", userId)
                .claim("role", role.name())
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }
}
