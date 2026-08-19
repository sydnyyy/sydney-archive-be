package com.sydneyarchive.global.config.security;

import com.sydneyarchive.auth.service.AuthPatternService;
import com.sydneyarchive.auth.service.TokenService;
import com.sydneyarchive.global.cookie.CookieUtils;
import com.sydneyarchive.global.security.filter.PatternValidationFilter;
import com.sydneyarchive.global.security.matcher.PatternValidationApiMatcher;
import com.sydneyarchive.global.security.oauth2.service.CustomOAuth2UserService;
import com.sydneyarchive.global.config.web.CorsProperties;
import com.sydneyarchive.global.security.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.sydneyarchive.global.security.filter.JwtAuthenticationFilter;
import com.sydneyarchive.global.security.handler.CustomAccessDeniedHandler;
import com.sydneyarchive.global.security.handler.CustomAuthenticationEntryPoint;
import com.sydneyarchive.global.security.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.sydneyarchive.global.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.sydneyarchive.global.security.provider.JwtAuthenticationProvider;
import com.sydneyarchive.global.security.jwt.JwtProvider;
import com.sydneyarchive.global.security.oauth2.resolver.LoginSessionStateAssignmentOAuth2AuthorizationRequestResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsProperties corsProperties;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final LoginSessionStateAssignmentOAuth2AuthorizationRequestResolver loginSessionStateAssignmentOAuth2AuthorizationRequestResolver;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public AuthenticationManager jwtAuthenticationManager(JwtAuthenticationProvider jwtAuthenticationProvider) {
        return new ProviderManager(jwtAuthenticationProvider);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            @Qualifier("jwtAuthenticationManager") AuthenticationManager authenticationManager,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            JwtProvider jwtProvider
    ) {
        return new JwtAuthenticationFilter(authenticationManager, customAuthenticationEntryPoint, jwtProvider);
    }

    @Bean
    public PatternValidationFilter patternValidationFilter(
            PatternValidationApiMatcher patternValidationApiMatcher,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            AuthPatternService authPatternService,
            CookieUtils cookieUtils,
            JwtProvider jwtProvider,
            TokenService tokenService
    ) {
        return new PatternValidationFilter(
                patternValidationApiMatcher,
                customAuthenticationEntryPoint,
                authPatternService,
                cookieUtils,
                jwtProvider,
                tokenService
        );
    }


    /**
     * Admin API
     *
     * /api/a/**
     */
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(
            HttpSecurity http,
            PatternValidationFilter patternValidationFilter,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        return http
                .securityMatcher("/api/a/**", "/oauth2/**", "/login/oauth2/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(patternValidationFilter, AnonymousAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, PatternValidationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/a/login/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/a/auth/token/issue").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/a/auth/logout").permitAll()
                        .requestMatchers("/api/a/**").hasRole("ADMIN")
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                                .authorizationRequestResolver(loginSessionStateAssignmentOAuth2AuthorizationRequestResolver)
                                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                        )
                        .userInfoEndpoint(userInfoEndPoint -> userInfoEndPoint
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .build();
    }

    /**
     * Guest API
     *
     * /api/g/**
     */
    @Bean
    @Order(2)
    public SecurityFilterChain guestSecurityFilterChain(
            HttpSecurity http,
            PatternValidationFilter patternValidationFilter,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {

        return http
                .securityMatcher("/api/g/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(management
                        -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(patternValidationFilter, AnonymousAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, PatternValidationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/g/auth/token/issue").permitAll()
                        .requestMatchers("/api/g/**").hasRole("GUEST")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .build();
    }

    /**
     * Common API
     *
     * /api/c/**
     */
    @Bean
    @Order(3)
    public SecurityFilterChain commonSecurityFilterChain(
            HttpSecurity http,
            PatternValidationFilter patternValidationFilter,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        return http
                .securityMatcher("/api/c/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(patternValidationFilter, AnonymousAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, PatternValidationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/c/items").permitAll()
                        .requestMatchers("/api/c/**").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .build();
    }

    /**
     * Public API
     *
     * /api/p/**
     * -> 인증 없음
     */
    @Bean
    @Order(4)
    public SecurityFilterChain publicSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        return http
                .securityMatcher("/api/p/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/p/**").permitAll()
                        .anyRequest().permitAll()
                )
                .build();
    }

    @Bean
    @Order(5)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(corsProperties.allowedOrigins()));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final String[] PUBLIC_ENDPOINTS = {
            "/",
            "/oauth2/**",
            "/api/public/**",
            "/ws/**",
            "/error"
    };
}
