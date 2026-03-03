package com.theforbiddenland.readingsession.service;

import com.theforbiddenland.readingsession.dto.ReadingSessionDto;
import com.theforbiddenland.readingsession.entity.ReadingSession;
import com.theforbiddenland.readingsession.enums.ReadingSessionStatus;
import com.theforbiddenland.readingsession.repository.ReadingSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadingSessionServiceTest {

    @Mock
    private ReadingSessionRepository readingSessionRepository;

    @InjectMocks
    private ReadingSessionService readingSessionService;

    private List<ReadingSession> createMockReadingSessions() {
        // OPEN 상태 (우선순위 1 + startDate 기준 정렬)
        ReadingSession open1 = ReadingSession.builder().id("1")
                .startDate(LocalDate.of(2025, 12, 10))
                .meetingDate(LocalDateTime.of(2025, 12, 30, 15, 00))
                .readingSessionStatus(ReadingSessionStatus.OPEN).build();
        ReadingSession open2 = ReadingSession.builder().id("2")
                .startDate(LocalDate.of(2025, 12, 1))
                .meetingDate(LocalDateTime.of(2025, 12, 20, 15, 00))
                .readingSessionStatus(ReadingSessionStatus.OPEN).build();

        // CLOSE 상태 (우선순위 2 + meetingDate 기준 정렬)
        ReadingSession close1 = ReadingSession.builder().id("3")
                .startDate(LocalDate.of(2025, 11, 1))
                .meetingDate(LocalDateTime.of(2025, 11, 2, 15, 00))
                .readingSessionStatus(ReadingSessionStatus.CLOSE).build();
        ReadingSession close2 = ReadingSession.builder().id("4")
                .startDate(LocalDate.of(2025, 11, 1))
                .meetingDate(LocalDateTime.of(2025, 11, 10, 15, 00))
                .readingSessionStatus(ReadingSessionStatus.CLOSE).build();

        // ONGOING 상태 (우선순위 0 + startDate 기준 정렬)
        ReadingSession ongoing1 = ReadingSession.builder().id("5")
                .startDate(LocalDate.of(2026, 1, 1))
                .meetingDate(LocalDateTime.of(2026, 1, 21, 15, 00))
                .readingSessionStatus(ReadingSessionStatus.ONGOING).build();
        ReadingSession ongoing2 = ReadingSession.builder().id("6")
                .startDate(LocalDate.of(2025, 12, 10))
                .meetingDate(LocalDateTime.of(2025, 12, 30, 15, 00))
                .readingSessionStatus(ReadingSessionStatus.ONGOING).build();

        // 정렬 순서가 섞인 목록 (Service에서 원하는 정렬을 하는지 확인하기 위함)
        return List.of(close1, open2, ongoing1, close2, open1, ongoing2);
    }

    @DisplayName("독서 세션 목록을 상태 우선순위와 날짜 기준으로 정렬하여 반환")
    @Test
    void getReadingSessions_shouldReturnSortedList() {
        // GIVEN
        List<ReadingSession> mockSessions = createMockReadingSessions();
        when(readingSessionRepository.findAll()).thenReturn(mockSessions);

        // WHEN
        List<ReadingSessionDto> result = readingSessionService.getReadingSessions();

        // THEN
        assertThat(result).hasSize(6);

        assertThat(result.get(0).id()).isEqualTo("6"); // ONGOING, 2025-12-10(startDate)
        assertThat(result.get(1).id()).isEqualTo("5"); // ONGOING, 2026-01-01(startDate)
        assertThat(result.get(2).id()).isEqualTo("2"); // OPEN, 2025-12-01(startDate)
        assertThat(result.get(3).id()).isEqualTo("1"); // OPEN, 2025-12-10(startDate)
        assertThat(result.get(4).id()).isEqualTo("4"); // CLOSE, 2025-11-10(meetingDate)
        assertThat(result.get(5).id()).isEqualTo("3"); // CLOSE, 2025-11-02(meetingDate)
    }
}