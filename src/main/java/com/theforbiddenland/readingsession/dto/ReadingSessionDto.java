package com.theforbiddenland.readingsession.dto;

import com.theforbiddenland.readingsession.entity.ReadingSession;
import com.theforbiddenland.readingsession.enums.ReadingSessionStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ReadingSessionDto (
        String id,

        String imageUrl,
        String title,
        String author,

        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime meetingAt,

        String description,

        ReadingSessionStatus readingSessionStatus,

        String purchaseLink,
        Integer currentReservations
) {

    public static ReadingSessionDto of(ReadingSession readingSession) {
        return ReadingSessionDto.builder()
                .id(readingSession.getId())
                .imageUrl(readingSession.getImageUrl())
                .title(readingSession.getTitle())
                .author(readingSession.getAuthor())
                .startDate(readingSession.getStartDate())
                .endDate(readingSession.getEndDate())
                .meetingAt(readingSession.getMeetingAt())
                .description(readingSession.getDescription())
                .readingSessionStatus(readingSession.getReadingSessionStatus())
                .purchaseLink(readingSession.getPurchaseLink())
                .currentReservations(readingSession.getCurrentReservations())
                .build();
    }
}
