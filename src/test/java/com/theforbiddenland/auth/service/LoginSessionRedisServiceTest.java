package com.theforbiddenland.auth.service;

import com.theforbiddenland.global.exception.LoginSessionException;
import com.theforbiddenland.global.exception.ErrorCode;
import com.theforbiddenland.support.IntegrationTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

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
        Assertions.assertThat(loginSessionRedisService.bindStateToLoginSession(sid, state1))
                .isTrue();

        String state2 = UUID.randomUUID().toString();
        Assertions.assertThat(loginSessionRedisService.bindStateToLoginSession(sid, state2))
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
}