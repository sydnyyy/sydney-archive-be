package com.theforbiddenland.readingsession.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReadingSessionCreateRequest (
        String imageUrl,
        String title,
        String author,

        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime meetingAt,

        String description,

        String purchaseLink
) { }
