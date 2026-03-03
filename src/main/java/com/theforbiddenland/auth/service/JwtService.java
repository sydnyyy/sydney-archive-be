package com.theforbiddenland.auth.service;

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
public class JwtService {

    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 2;
    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 14;

    private final JwtProperties jwtProperties;

    public String generateAccessToken(String clientId, Role role) {
        return generateToken(clientId, role, ACCESS_TOKEN_VALIDITY_SECONDS);
    }

    public String generateRefreshToken(String clientId, Role role) {
        return generateToken(clientId, role, REFRESH_TOKEN_VALIDITY_SECONDS);
    }

    private String generateToken(String clientId, Role role, long validityInSeconds) {
        if (clientId == null || clientId.isBlank()) {
            throw new JwtCreationException("clientId is required");
        }

        if (role == null) {
            throw new JwtCreationException("role is required");
        }

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInSeconds * 1000);

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(clientId)
                .issuedAt(now)
                .expiration(expiry)
                .claim("clientId", clientId)
                .claim("role", role.name())
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }
}
