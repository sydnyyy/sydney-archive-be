package com.sydneyarchive.common.sse.api;

import com.sydneyarchive.common.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/p/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping("/connect")
    public SseEmitter connect(@RequestParam String sid) {
        return sseService.connect(sid);
    }
}
