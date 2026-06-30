package com.sydneyarchive.auth.service;

import com.sydneyarchive.auth.enums.Platform;
import com.sydneyarchive.global.exception.LoginSessionException;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.support.IntegrationTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginSessionRedisServiceTest extends IntegrationTestSupport {

    @Autowired
    private LoginSessionRedisService loginSessionRedisService;

    @Test
    @DisplayName("인증 세션에 state가 할당되어있다면, 수정 작업 없이 false 리턴")
    void should_return_false_when_state_assigned() {
        String sid = UUID.randomUUID().toString();
        String authCode = UUID.randomUUID().toString();
        loginSessionRedisService.saveLoginSession(sid, authCode);

        String state1 = UUID.randomUUID().toString();
        Assertions
                .assertThat(loginSessionRedisService.bindStateAndPlatformToLoginSession(sid, state1, Platform.NONE))
                .isTrue();

        String state2 = UUID.randomUUID().toString();
        Assertions
                .assertThat(loginSessionRedisService.bindStateAndPlatformToLoginSession(sid, state2, Platform.NONE))
                .isFalse();
    }

    @Test
    @DisplayName("세션 검증 시, 버전이 일치하지 않으면 AUTH_VERSION_MISMATCH 예외 발생")
    void should_throwException_when_sessionVersionMismatches() {
        String sid = UUID.randomUUID().toString();
        String authCode = UUID.randomUUID().toString();
        loginSessionRedisService.saveLoginSession(sid, authCode);

        // version default 설정: 0
        int wrongVersion = 3;

        assertThatThrownBy(() -> loginSessionRedisService.verifySessionAndGetUserId(sid, wrongVersion, authCode))
                .isInstanceOf(LoginSessionException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.LOGIN_VERSION_MISMATCH);
    }

    @Test
    @DisplayName("세션 검증 시, 인증 코드가 일치하지 않으면 AUTH_CODE_MISMATCH 예외 발생")
    void should_throwException_when_authCodeMismatches() {
        String sid = UUID.randomUUID().toString();
        String authCode = UUID.randomUUID().toString();
        loginSessionRedisService.saveLoginSession(sid, authCode);

        String wrongAuthCode = authCode + "_invalid";
        assertThatThrownBy(() -> loginSessionRedisService.verifySessionAndGetUserId(sid, 0, wrongAuthCode))
                .isInstanceOf(LoginSessionException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTH_CODE_MISMATCH);
    }

    @Test
    @DisplayName("세션 검증 성공 후, 반환 값인 userId가 null이면 USER_ID_MISSING 예외 발생")
    void should_throwException_when_userIdIsNull() {
        String sid = UUID.randomUUID().toString();
        String authCode = UUID.randomUUID().toString();
        loginSessionRedisService.saveLoginSession(sid, authCode);  // userId 할당하지 않은 상태

        assertThatThrownBy(() -> loginSessionRedisService.verifySessionAndGetUserId(sid, 0, authCode))
                .isInstanceOf(LoginSessionException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_ID_MISSING);
    }

    @Test
    @DisplayName("LoginSession에 Binding 작업이 발생하지 않았다면 버전 조회 시 0 리턴")
    void should_return_0_when_only_query() {
        String sid = UUID.randomUUID().toString();
        String authCode = UUID.randomUUID().toString();
        loginSessionRedisService.saveLoginSession(sid, authCode);

        assertThat(loginSessionRedisService.getLoginSessionVersion(sid)).isEqualTo(0);
        assertThat(loginSessionRedisService.getLoginSessionVersion(sid)).isEqualTo(0);
    }

    @Test
    @DisplayName("LoginSession에 Binding 작업이 발생했으면 버전 조회 시 1 이상 리턴")
    void should_return_positive_value_when_binding_occurs() {
        String sid = UUID.randomUUID().toString();
        String authCode = UUID.randomUUID().toString();
        loginSessionRedisService.saveLoginSession(sid, authCode);

        assertThat(loginSessionRedisService.getLoginSessionVersion(sid)).isEqualTo(0);

        String state = UUID.randomUUID().toString();
        boolean isAssigned = loginSessionRedisService.bindStateAndPlatformToLoginSession(sid, state, Platform.NONE);
        if (isAssigned)
            assertThat(loginSessionRedisService.getLoginSessionVersion(sid)).isGreaterThan(0);

        String userId = UUID.randomUUID().toString();
        isAssigned = loginSessionRedisService.assignUserIdToLoginSession(state, userId);
        if (isAssigned)
            assertThat(loginSessionRedisService.getLoginSessionVersion(sid)).isGreaterThan(0);
    }
}