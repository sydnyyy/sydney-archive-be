package com.sydneyarchive.global.security.provider;

import com.sydneyarchive.auth.security.UserPrincipal;
import com.sydneyarchive.global.exception.CustomAuthenticationException;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.security.jwt.JwtProvider;
import com.sydneyarchive.global.security.jwt.JwtAuthenticationToken;
import com.sydneyarchive.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtProvider jwtProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accessToken = (String) authentication.getCredentials();

        if (accessToken == null || accessToken.isEmpty()) {
            throw new CustomAuthenticationException(ErrorCode.ACCESS_TOKEN_MISSING);
        }

        try {
            String userId = jwtProvider.getClaimUserId(accessToken);
            Role role = jwtProvider.getClaimRole(accessToken);

            UserPrincipal userPrincipal = UserPrincipal.builder()
                    .userId(userId)
                    .role(role)
                    .build();

            Set<SimpleGrantedAuthority> authorities = getAuthoritiesFromRole(role);
            return new JwtAuthenticationToken(userPrincipal, authorities);
        } catch (Exception e) {
            throw new CustomAuthenticationException(ErrorCode.ACCESS_TOKEN_EXPIRED);
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

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
