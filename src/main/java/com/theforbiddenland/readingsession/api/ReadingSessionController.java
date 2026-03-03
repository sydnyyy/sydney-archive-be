package com.theforbiddenland.readingsession.api;

import com.theforbiddenland.readingsession.dto.ReadingSessionCreateRequest;
import com.theforbiddenland.readingsession.dto.ReadingSessionDto;
import com.theforbiddenland.readingsession.service.ReadingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<?> createReadingSession(@RequestBody ReadingSessionCreateRequest readingSessionCreateRequest) {
        ReadingSessionDto readingSessionDto = readingSessionService.createReadingSession(readingSessionCreateRequest);
        return ResponseEntity.ok(readingSessionDto);
    }
}
