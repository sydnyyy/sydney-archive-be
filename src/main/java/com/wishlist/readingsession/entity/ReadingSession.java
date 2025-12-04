package com.wishlist.readingsession.entity;

import com.wishlist.readingsession.dto.ReadingSessionCreateRequest;
import com.wishlist.readingsession.enums.ReadingSessionStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "reading_sessions")
@Builder
@Getter
public class ReadingSession {

    @Id
    private String id;

    private String imageUrl;
    private String title;
    private String author;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime meetingDate;

    private ReadingSessionStatus readingSessionStatus;

    private String purchaseLink;
    private Integer currentReservations;

    public static ReadingSession of(ReadingSessionCreateRequest readingSessionCreateRequest) {
        return ReadingSession.builder()
                .imageUrl(readingSessionCreateRequest.imageUrl())
                .title(readingSessionCreateRequest.title())
                .author(readingSessionCreateRequest.author())
                .startDate(readingSessionCreateRequest.startDate())
                .endDate(readingSessionCreateRequest.endDate())
                .meetingDate(readingSessionCreateRequest.meetingDate())
                .readingSessionStatus(ReadingSessionStatus.OPEN)
                .purchaseLink(readingSessionCreateRequest.purchaseLink())
                .currentReservations(0)
                .build();
    }
}
