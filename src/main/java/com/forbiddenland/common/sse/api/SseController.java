package com.forbiddenland.common.sse.api;

import com.forbiddenland.common.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping("/connect")
    public SseEmitter connect(@RequestParam String sid) {
        return sseService.connect(sid);
    }
}
