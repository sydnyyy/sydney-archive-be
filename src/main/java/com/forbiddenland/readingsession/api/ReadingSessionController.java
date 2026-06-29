package com.forbiddenland.readingsession.api;

import com.forbiddenland.readingsession.dto.ReadingSessionCreateRequest;
import com.forbiddenland.readingsession.dto.ReadingSessionDto;
import com.forbiddenland.readingsession.service.ReadingSessionService;
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
