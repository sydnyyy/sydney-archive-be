package com.sydneyarchive.global.security.filter;

import com.sydneyarchive.global.security.dto.JwtAuthenticationToken;
import com.sydneyarchive.global.security.handler.CustomAuthenticationEntryPoint;
import com.sydneyarchive.global.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtUtil jwtUtil;

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
            Authentication authenticationRequest = new JwtAuthenticationToken(accessToken);
            Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);

            SecurityContext securityContext = securityContextHolderStrategy.createEmptyContext();
            securityContext.setAuthentication(authenticationResult);
            securityContextHolderStrategy.setContext(securityContext);

        } catch (AuthenticationException e) {
            authenticationEntryPoint.commence(request, response, e);
            return;
        } catch (Exception ignored) {}

        filterChain.doFilter(request, response);
    }
}
