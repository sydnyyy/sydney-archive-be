package com.wishlist.readingsession.api;

import com.wishlist.readingsession.dto.ReadingSessionDto;
import com.wishlist.readingsession.service.ReadingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reading-sessions")
public class ReadingSessionController {

    private final ReadingSessionService readingSessionService;

    @GetMapping
    public ResponseEntity<?> getReadingSessions() {
        List<ReadingSessionDto> readingSessions = readingSessionService.getReadingSessions();
        return ResponseEntity.ok(readingSessions);
    }
}
