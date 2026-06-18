package com.theforbiddenland.auth.service;

import com.theforbiddenland.support.IntegrationTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

class AuthSessionRedisServiceTest extends IntegrationTestSupport {

    @Autowired
    private AuthSessionRedisService authSessionRedisService;

    @Test
    @DisplayName("인증 세션에 state가 할당되어있다면, 수정 작업 없이 false 리턴")
    void should_return_false_when_state_assigned() {
        String sid = UUID.randomUUID().toString();
        String authCode = UUID.randomUUID().toString();
        authSessionRedisService.saveAuthSession(sid, authCode);

        String state1 = UUID.randomUUID().toString();
        Assertions.assertThat(authSessionRedisService.bindStateToAuthSession(sid, state1))
                .isTrue();

        String state2 = UUID.randomUUID().toString();
        Assertions.assertThat(authSessionRedisService.bindStateToAuthSession(sid, state2))
                .isFalse();
    }
}