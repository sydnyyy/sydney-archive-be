package com.forbiddenland.readingsession.entity;

import com.forbiddenland.readingsession.dto.ReadingSessionCreateRequest;
import com.forbiddenland.readingsession.enums.ReadingSessionStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "reading_sessions")
@CompoundIndex(
        name = "unique_author_title",
        def = "{'author': 1, 'title': 1}",
        unique = true
)
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
    private LocalDateTime meetingAt;

    private String description;

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
                .meetingAt(readingSessionCreateRequest.meetingAt())
                .description(readingSessionCreateRequest.description())
                .readingSessionStatus(ReadingSessionStatus.OPEN)
                .purchaseLink(readingSessionCreateRequest.purchaseLink())
                .currentReservations(0)
                .build();
    }
}
