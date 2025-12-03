package com.wishlist.readingsession.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "reading_sessions")
public class ReadingSession {

    @Id
    private String id;

    private String imageUrl;
    private String title;
    private String author;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime meetingDate;

    private String purchaseLink;
    private Integer currentReservations;
}
