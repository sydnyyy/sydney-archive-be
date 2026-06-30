package com.sydneyarchive.auth.api;

import com.sydneyarchive.auth.dto.request.AccessTokenIssueRequest;
import com.sydneyarchive.auth.service.LoginSessionService;
import com.sydneyarchive.auth.service.TokenService;
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
    private final LoginSessionService loginSessionService;

    @PostMapping("/token/issue")
    public ResponseEntity<?> issueAccessToken(
            @RequestBody(required = false) AccessTokenIssueRequest accessTokenIssueRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (accessTokenIssueRequest != null && accessTokenIssueRequest.loginSessionSid() != null) {
            loginSessionService.terminateLoginSession(accessTokenIssueRequest.loginSessionSid());
        }

        String accessToken = tokenService.issueAccessToken(request, response);
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
