package com.theforbiddenland.auth.api;

import com.theforbiddenland.auth.dto.internal.AuthSessionContext;
import com.theforbiddenland.auth.dto.response.AuthSessionResponse;
import com.theforbiddenland.auth.service.AuthSessionService;
import com.theforbiddenland.auth.service.TokenService;
import com.theforbiddenland.global.cookie.CookieUtil;
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

    private final AuthSessionService authSessionService;
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;

    @PostMapping("/sessions")
    public ResponseEntity<?> getAuthSession(HttpServletResponse httpServletResponse) {
        AuthSessionContext authSessionContext = authSessionService.generateAuthSession();

        if (authSessionContext.qrCodeBase64() != null) {
            cookieUtil.setAuthCodeCookie(httpServletResponse, authSessionContext.authCode());
        }
        return ResponseEntity.ok(AuthSessionResponse.of(authSessionContext));
    }

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
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        tokenService.logout(request, response);
        return ResponseEntity.noContent().build();
    }
}
