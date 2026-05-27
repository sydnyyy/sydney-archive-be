package com.theforbiddenland.global.security.jwt;

import com.theforbiddenland.auth.security.UserPrincipal;
import com.theforbiddenland.global.exception.ErrorCode;
import com.theforbiddenland.global.exception.JwtAuthException;
import com.theforbiddenland.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter {

    private final JwtUtil jwtUtil;

    public Authentication getAuthentication(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new JwtAuthException(ErrorCode.ACCESS_TOKEN_MISSING);
        }

        try {
            String userId = jwtUtil.getClaimUserId(accessToken);
            Role role = jwtUtil.getClaimRole(accessToken);

            UserPrincipal userPrincipal = UserPrincipal.builder()
                    .userId(userId)
                    .role(role)
                    .build();

            Set<SimpleGrantedAuthority> authorities = getAuthoritiesFromRole(role);
            return new UsernamePasswordAuthenticationToken(userPrincipal, accessToken, authorities);
        } catch (Exception e) {
            throw new JwtAuthException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        }
    }

    private Set<SimpleGrantedAuthority> getAuthoritiesFromRole(Role role) {
        if (role == Role.ADMIN) {
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        else if(role == Role.USER) {
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_GUEST"));
    }
}
