package com.sydneyarchive.global.security.filter;

import com.sydneyarchive.auth.service.AuthPatternService;
import com.sydneyarchive.auth.service.TokenService;
import com.sydneyarchive.global.cookie.CookieUtils;
import com.sydneyarchive.global.exception.CookieException;
import com.sydneyarchive.global.exception.CustomAuthenticationException;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.security.handler.CustomAuthenticationEntryPoint;
import com.sydneyarchive.global.security.jwt.JwtProvider;
import com.sydneyarchive.global.security.matcher.PatternValidationApiMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class PatternValidationFilter extends OncePerRequestFilter {

    private static final String PATTERN_HEADER = "Pattern";
    private static final String PATTERN_SLOT_HEADER = "Pattern-Slot";

    private final PatternValidationApiMatcher patternValidationApiMatcher;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final AuthPatternService authPatternService;
    private final CookieUtils cookieUtils;
    private final JwtProvider jwtProvider;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String refreshToken = null;
        try {
            refreshToken = cookieUtils.getCookie(CookieUtils.REFRESH_TOKEN_COOKIE_NAME, request);
        } catch (CookieException ignored) {}

        String pattern = request.getHeader(PATTERN_HEADER);
        String patternSlot = request.getHeader(PATTERN_SLOT_HEADER);

        if (refreshToken == null
                || !patternValidationApiMatcher.matches(request)
                || pattern == null || pattern.isBlank()
                || patternSlot == null || patternSlot.isBlank()
        ) {
            doFilter(request, response, filterChain);
            return;
        }

        String userId = jwtProvider.getSubject(refreshToken);
        String familyId = jwtProvider.getClaimFamilyId(refreshToken);

        if (!authPatternService.validatePattern(userId, pattern, patternSlot)) {
            tokenService.revokeFamily(familyId);
            authPatternService.deletePattern(userId);

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new CustomAuthenticationException(ErrorCode.PATTERN_MISMATCH)
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
