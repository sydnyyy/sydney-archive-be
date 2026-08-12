package com.sydneyarchive.auth.api;

import com.sydneyarchive.auth.dto.request.LoginSessionCompleteRequest;
import com.sydneyarchive.auth.dto.request.LoginSessionRequest;
import com.sydneyarchive.auth.dto.response.LoginSessionResponse;
import com.sydneyarchive.auth.service.LoginSessionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/login/sessions")
@RequiredArgsConstructor
public class AdminLoginController {

    private final LoginSessionService loginSessionService;

    @PostMapping
    public ResponseEntity<?> getLoginSession(@RequestBody LoginSessionRequest loginSessionRequest) {
        if (loginSessionRequest.previousSid() != null) {
            loginSessionService.terminateLoginSession(loginSessionRequest.previousSid());
        }

        LoginSessionResponse response
                = loginSessionService.generateLoginSession(loginSessionRequest.secret());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<?> isAvailableLoginSession(@RequestParam String sid) {
        boolean isAvailable = loginSessionService.isAvailableLoginSession(sid);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }

    @PostMapping("/complete")
    public ResponseEntity<?> verifyLoginSessionAndIssueRefreshToken(
            @RequestBody LoginSessionCompleteRequest loginSessionCompleteRequest,
            HttpServletResponse httpServletResponse
    ) {
        loginSessionService.verifyLoginSessionAndIssueRefreshToken(
                loginSessionCompleteRequest, httpServletResponse
        );

        return ResponseEntity.ok().build();
    }
}
