package com.forbiddenland.global.security.filter;

import com.forbiddenland.global.exception.CustomAuthenticationException;
import com.forbiddenland.global.exception.JwtAuthException;
import com.forbiddenland.global.security.handler.OAuth2AuthenticationEntryPoint;
import com.forbiddenland.global.security.jwt.JwtAuthenticationConverter;
import com.forbiddenland.global.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint;

    private final SecurityContextHolderStrategy securityContextHolderStrategy
            = SecurityContextHolder.getContextHolderStrategy();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String accessToken = jwtUtil.getAccessToken(request);

        if (accessToken == null || accessToken.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication authentication = jwtAuthenticationConverter.getAuthentication(accessToken);

            SecurityContext securityContext = securityContextHolderStrategy.createEmptyContext();
            securityContext.setAuthentication(authentication);
            securityContextHolderStrategy.setContext(securityContext);

            filterChain.doFilter(request, response);
        } catch (JwtAuthException e) {
            oAuth2AuthenticationEntryPoint.commence(
                    request, response,
                    new CustomAuthenticationException(e.getErrorCode())
            );
        }
    }
}
