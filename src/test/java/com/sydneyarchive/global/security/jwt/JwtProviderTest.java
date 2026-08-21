package com.sydneyarchive.global.security.jwt;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.exception.JwtAuthException;
import com.sydneyarchive.support.IntegrationTestSupport;
import com.sydneyarchive.user.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtProviderTest extends IntegrationTestSupport {

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("토큰 생성 시 userId가 null이면 JwtAuthException(USER_ID_REQUIRED) 예외 발생")
    public void shouldThrowException_WhenUserIdIsNullOnGenerateToken() {
        assertThatThrownBy(() -> jwtProvider.generateAccessToken(null, Role.ADMIN))
                .isInstanceOf(JwtAuthException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_ID_REQUIRED);
    }

    @Test
    @DisplayName("토큰 생성 시 role이 null이면 JwtAuthException(ROLE_REQUIRED) 예외 발생")
    public void shouldThrowException_WhenRoleIsNullOnGenerateToken() {
        assertThatThrownBy(() -> jwtProvider.generateRefreshToken(NanoIdUtils.randomNanoId(), null, NanoIdUtils.randomNanoId()))
                .isInstanceOf(JwtAuthException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROLE_REQUIRED);
    }

    @Test
    @DisplayName("리프레시 토큰 생성 시 familyId이 null이면 JwtAuthException(FAMILY_ID_REQUIRED) 예외 발생")
    public void shouldThrowException_WhenFamilyIdIsNullOnGenerateRefreshToken() {
        assertThatThrownBy(() -> jwtProvider.generateRefreshToken(NanoIdUtils.randomNanoId(), Role.ADMIN, null))
                .isInstanceOf(JwtAuthException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAMILY_ID_REQUIRED);
    }

    @Test
    @DisplayName("액세스 토큰 생성 시 내부에 저장된 Claim(userId, role) 정보 추출 가능")
    public void extractClaimsFromAccessToken() {
        String userId = NanoIdUtils.randomNanoId();
        Role role = Role.USER;

        String accessToken = jwtProvider.generateAccessToken(userId, role);

        assertThat(jwtProvider.getSubject(accessToken)).isEqualTo(userId);
        assertThat(jwtProvider.getClaimRole(accessToken)).isEqualTo(role);
    }

    @Test
    @DisplayName("Authorization 헤더에 올바른 Bearer 토큰이 포함되어 있으면 토큰 정상 추출")
    public void shouldExtractAccessToken_WhenValidBearerHeaderProvided() {
        String userId = NanoIdUtils.randomNanoId();
        Role role = Role.ADMIN;
        String accessToken = jwtProvider.generateAccessToken(userId, role);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(
                JwtProvider.AUTHORIZATION_HEADER,
                JwtProvider.BEARER_TOKEN_PREFIX + accessToken
        );

        assertThat(jwtProvider.getAccessToken(request)).isEqualTo(accessToken);
    }

    @Test
    @DisplayName("Authorization 헤더 형식이 잘못되었거나 헤더명이 다르면 null 반환")
    public void shouldReturnNull_WhenHeaderFormatIsInvalid() {
        String userId = NanoIdUtils.randomNanoId();
        Role role = Role.ADMIN;
        String accessToken = jwtProvider.generateAccessToken(userId, role);

        MockHttpServletRequest request1 = new MockHttpServletRequest();
        request1.addHeader(
                JwtProvider.AUTHORIZATION_HEADER,
                "Bearer" + accessToken
        );

        MockHttpServletRequest request2 = new MockHttpServletRequest();
        request1.addHeader(
                "Authorization2",
                "Bearer " + accessToken
        );

        assertThat(jwtProvider.getAccessToken(request1)).isNull();
        assertThat(jwtProvider.getAccessToken(request2)).isNull();
    }
}