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
            @RequestParam(required = false) String sid,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String accessToken = tokenService.issueAccessToken(sid, request, response);
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        tokenService.logout(request, response);
        return ResponseEntity.noContent().build();
    }
}
