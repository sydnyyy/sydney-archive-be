package com.sydneyarchive.auth.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.sydneyarchive.support.IntegrationTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthPatternServiceTest extends IntegrationTestSupport {

    @Autowired
    private AuthPatternService authPatternService;

    @DisplayName("slot-0 EMPTY 상태 -> slot-0 패턴 검증 요청 시 permit")
    @Test
    void permitSlot0PatternWhenSlot0Empty() {
        String userId = NanoIdUtils.randomNanoId();
        String slot0Pattern = "abc";
        String slot0 = "0";

        Assertions
                .assertThat(authPatternService.validatePattern(userId, slot0Pattern, slot0))
                .isTrue();

        authPatternService.deletePattern(userId);
    }

    @DisplayName("slot-0 EMPTY 상태 -> slot-1 패턴 검증 요청 시 deny")
    @Test
    void denySlot1PatternWhenSlot0Empty() {
        String userId = NanoIdUtils.randomNanoId();
        String slot1Pattern = "abc";
        String slot1 = "1";

        Assertions
                .assertThat(authPatternService.validatePattern(userId, slot1Pattern, slot1))
                .isFalse();

        authPatternService.deletePattern(userId);
    }

    @DisplayName("slot-0 abc1234 패턴 -> slot-0 abc1234xyz 패턴 검증 요청 시 permit")
    @Test
    void permitExtendedPatternAfterDelay() {
        String userId = NanoIdUtils.randomNanoId();
        String slot0Pattern = "abc1234";
        String slot0 = "0";

        authPatternService.validatePattern(userId, slot0Pattern, slot0);

        Assertions
                .assertThat(
                        authPatternService.validatePattern(
                                userId, slot0Pattern + "xyz", slot0
                        )
                )
                .isTrue();

        authPatternService.deletePattern(userId);
    }

    @DisplayName("slot-0 abc1234 패턴 -> slot-0 abc12 패턴(네트워크 지연) 검증 요청 시 permit")
    @Test
    void permitShortenedPatternAfterDelay() {
        String userId = NanoIdUtils.randomNanoId();
        String slot0Pattern = "abc1234";
        String slot0 = "0";

        authPatternService.validatePattern(userId, slot0Pattern, slot0);

        Assertions
                .assertThat(
                        authPatternService.validatePattern(
                                userId, slot0Pattern.substring(0, slot0Pattern.length() - 2), slot0
                        )
                )
                .isTrue();

        authPatternService.deletePattern(userId);
    }

    @DisplayName("패턴 재사용 시 deny")
    @Test
    void enyWhenReusingSamePattern() {
        String userId = NanoIdUtils.randomNanoId();
        String slot0Pattern = "abc1234";
        String slot0 = "0";

        authPatternService.validatePattern(userId, slot0Pattern, slot0);

        Assertions
                .assertThat(authPatternService.validatePattern(userId, slot0Pattern, slot0))
                .isFalse();
    }

    /*
     * 테스트 환경 설정
     * - Pattern 최대 길이: 10
     * - Pattern 처리율 충족 기준: 80%
     */
    @DisplayName("slot-0 처리율 충족 -> 새로운 패턴 slot-1 검증 요청 시 permit")
    @Test
    void permitSlot1WhenSlot0RateMet() {
        String userId = NanoIdUtils.randomNanoId();
        String slot0Pattern = "0123456789";
        String slot0 = "0";

        for(int i = 0; i < slot0Pattern.length() ;i++) {
            authPatternService.validatePattern(userId, slot0Pattern.substring(0, i + 1), slot0);
        }

        String slot1Pattern = "xyx123";
        String slot1 = "1";

        Assertions
                .assertThat(authPatternService.validatePattern(userId, slot1Pattern, slot1))
                .isTrue();

        authPatternService.deletePattern(userId);
    }

    /*
     * 테스트 환경 설정
     * - Pattern 최대 길이: 10
     * - Pattern 처리율 충족 기준: 80%
     */
    @DisplayName("slot-0 처리율 미충족(30% 설정) -> slot-1 패턴 검증 요청 시 deny")
    @Test
    void denySlot1WhenSlot0RateNotMet() {
        String userId = NanoIdUtils.randomNanoId();
        String slot0Pattern = "0123456789";
        String slot0 = "0";

        // slot-0 패턴 설정 & (처리율 30% 설정)
        for(int i = 0; i < 3; i++) {
            authPatternService.validatePattern(userId, slot0Pattern.substring(0, i + 1), slot0);
        }

        String slot1Pattern = "abc678";
        String slot1 = "1";
        Assertions
                .assertThat(authPatternService.validatePattern(userId, slot1Pattern, slot1))
                .isFalse();

        authPatternService.deletePattern(userId);
    }

    /*
     * 테스트 환경 설정
     * - Pattern 최대 길이: 10
     * - Pattern 처리율 충족 기준: 80%
     */
    @DisplayName("slot-0 처리율(90%) 충족 & slot-1 처리율(30%) 미충족 -> slot-2 abc678 패턴 검증 요청 시 deny")
    @Test
    void denySlot2WhenSlot0RateMetAndSlot1RateNotMet() {
        String userId = NanoIdUtils.randomNanoId();
        String slot0Pattern = "0123456789";
        String slot0 = "0";

        // slot-0 패턴 설정 (처리율 90% 설정)
        for(int i = 0; i < slot0Pattern.length() - 1; i++) {
            authPatternService.validatePattern(userId, slot0Pattern.substring(0, i + 1), slot0);
        }

        String slot1Pattern = "abcdefghik";
        String slot1 = "1";
        // slot-1 패턴 설정 (처리율 30% 설정)
        for(int i = 0; i < 3; i++) {
            authPatternService.validatePattern(userId, slot1Pattern.substring(0, i + 1), slot1);
        }

        String slot2Pattern = "abc678";
        String slot2 = "2";
        Assertions
                .assertThat(authPatternService.validatePattern(userId, slot2Pattern, slot2))
                .isFalse();

        authPatternService.deletePattern(userId);
    }
}