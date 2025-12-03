package com.wishlist.readingsession.dto;

import com.wishlist.readingsession.entity.ReadingSession;
import com.wishlist.readingsession.enums.ReadingSessionStatus;
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
        LocalDateTime meetingDate,

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
                .meetingDate(readingSession.getMeetingDate())
                .readingSessionStatus(readingSession.getReadingSessionStatus())
                .purchaseLink(readingSession.getPurchaseLink())
                .currentReservations(readingSession.getCurrentReservations())
                .build();
    }
}
