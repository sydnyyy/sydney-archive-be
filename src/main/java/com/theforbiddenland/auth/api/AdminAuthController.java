package com.theforbiddenland.auth.api;

import com.theforbiddenland.auth.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final JwtService jwtService;

    @PostMapping("/token/issue")
    public ResponseEntity<?> issueAccessToken(
            @RequestParam String sid,
            HttpServletRequest request
    ) {
        String accessToken = jwtService.issueAccessToken(sid, request);
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }
}
