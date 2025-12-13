package com.wishlist.readingsession.service;

import com.wishlist.global.exception.DuplicateReadingSessionException;
import com.wishlist.readingsession.dto.ReadingSessionCreateRequest;
import com.wishlist.readingsession.dto.ReadingSessionDto;
import com.wishlist.readingsession.entity.ReadingSession;
import com.wishlist.readingsession.enums.ReadingSessionStatus;
import com.wishlist.readingsession.repository.ReadingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadingSessionService {

    private final ReadingSessionRepository readingSessionRepository;

    /**
     * ONGOING: 우선순위 0, 시작일 오름차순
     * OPEN: 우선순위 1, 시작일 오름차순
     * CLOSE: 우선순위 2, 미팅일 내림차순
     * @return 정렬된 ReadingSessionDto 리스트
     */
    public List<ReadingSessionDto> getReadingSessions() {
        return readingSessionRepository.findAll()
                .stream()
                .map(ReadingSessionDto::of)
                .sorted((a, b) -> {
                    int p = Integer.compare(
                            a.readingSessionStatus().getPriority(),
                            b.readingSessionStatus().getPriority()
                    );
                    if (p != 0) return p;

                    if (a.readingSessionStatus() == ReadingSessionStatus.CLOSE) {
                        return Comparator.<LocalDateTime>nullsLast(Comparator.reverseOrder())
                                .compare(a.meetingAt(), b.meetingAt());
                    }
                    return Comparator.<LocalDate>nullsLast(Comparator.naturalOrder())
                            .compare(a.startDate(), b.startDate());
                })
                .toList();
    }

    public ReadingSessionDto createReadingSession(ReadingSessionCreateRequest readingSessionCreateRequest) {
        ReadingSession readingSession = ReadingSession.of(readingSessionCreateRequest);
        try {
            readingSessionRepository.save(readingSession);
        } catch (DuplicateKeyException e) {
            throw new DuplicateReadingSessionException();
        }
        return ReadingSessionDto.of(readingSession);
    }
}
