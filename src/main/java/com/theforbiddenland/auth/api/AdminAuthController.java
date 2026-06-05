package com.theforbiddenland.auth.api;

import com.theforbiddenland.auth.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final TokenService tokenService;

    @PostMapping("/token/issue")
    public ResponseEntity<?> issueAccessToken(
            @RequestParam String sid,
            HttpServletRequest request
    ) {
        String accessToken = tokenService.issueAccessToken(sid, request);
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }
}
