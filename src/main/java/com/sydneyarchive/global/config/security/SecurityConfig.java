package com.sydneyarchive.global.config.security;

import com.sydneyarchive.auth.service.CustomOAuth2UserService;
import com.sydneyarchive.global.config.web.CorsProperties;
import com.sydneyarchive.global.security.cookie.OAuth2AuthorizationRequestRepository;
import com.sydneyarchive.global.security.filter.JwtAuthenticationFilter;
import com.sydneyarchive.global.security.handler.CustomAccessDeniedHandler;
import com.sydneyarchive.global.security.handler.CustomAuthenticationEntryPoint;
import com.sydneyarchive.global.security.handler.OAuth2AuthenticationFailureHandler;
import com.sydneyarchive.global.security.handler.OAuth2AuthenticationSuccessHandler;
import com.sydneyarchive.global.security.jwt.JwtAuthenticationProvider;
import com.sydneyarchive.global.security.jwt.JwtUtil;
import com.sydneyarchive.global.security.resolver.LoginSessionStateAssignmentOAuth2AuthorizationRequestResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    private final OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

    @Bean
    public AuthenticationManager authenticationManager(JwtAuthenticationProvider jwtProvider) {
        return new ProviderManager(jwtProvider);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            AuthenticationManager authenticationManager,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            JwtUtil jwtUtil
    ) {
        return new JwtAuthenticationFilter(
                authenticationManager, customAuthenticationEntryPoint, jwtUtil
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(management
                        -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, AnonymousAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/chat/rooms").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/chat/rooms/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/items").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/items/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/items/**").hasRole("ADMIN")
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin/login/sessions").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/admin/login/sessions/status").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin/login/sessions/complete").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin/auth/token/issue").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/sid").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/items").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/chat/messages").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sse/connect").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/access").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(authorizationEndpoint
                                -> authorizationEndpoint
                                .authorizationRequestResolver(loginSessionStateAssignmentOAuth2AuthorizationRequestResolver)
                                .authorizationRequestRepository(oAuth2AuthorizationRequestRepository)
                        )
                        .userInfoEndpoint(userInfoEndPoint
                                -> userInfoEndPoint.userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .exceptionHandling(ex
                        -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
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
