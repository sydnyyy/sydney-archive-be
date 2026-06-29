package com.theforbiddenland.auth.api;

import com.theforbiddenland.auth.dto.internal.LoginSessionContext;
import com.theforbiddenland.auth.dto.request.LoginSessionCompleteRequest;
import com.theforbiddenland.auth.dto.request.LoginSessionRequest;
import com.theforbiddenland.auth.dto.response.LoginSessionResponse;
import com.theforbiddenland.auth.service.LoginSessionService;
import com.theforbiddenland.global.cookie.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.theforbiddenland.global.cookie.CookieUtil.AUTH_CODE_COOKIE_NAME;

@RestController
@RequestMapping("/api/admin/login/sessions")
@RequiredArgsConstructor
public class AdminLoginController {

    private final LoginSessionService loginSessionService;
    private final CookieUtil cookieUtil;

    @PostMapping
    public ResponseEntity<?> getLoginSession(
            @RequestBody(required = false) LoginSessionRequest loginSessionRequest,
            HttpServletResponse httpServletResponse) {

        if (loginSessionRequest != null && loginSessionRequest.previousSid() != null) {
            loginSessionService.terminateLoginSession(loginSessionRequest.previousSid());
        }

        LoginSessionContext loginSessionContext = loginSessionService.generateLoginSession();

        if (loginSessionContext.authCode() != null) {
            cookieUtil.setAuthCodeCookie(httpServletResponse, loginSessionContext.authCode());
        }
        return ResponseEntity.ok(LoginSessionResponse.of(loginSessionContext));
    }

    @GetMapping("/status")
    public ResponseEntity<?> isAvailableLoginSession(@RequestParam String sid) {
        boolean isAvailable = loginSessionService.isAvailableLoginSession(sid);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }

    @PostMapping("/complete")
    public ResponseEntity<?> verifyLoginSessionAndIssueRefreshToken(
            @RequestBody LoginSessionCompleteRequest loginSessionCompleteRequest,
            @CookieValue(name = AUTH_CODE_COOKIE_NAME) String authCode,
            HttpServletResponse httpServletResponse
    ) {
        loginSessionService.verifyLoginSessionAndIssueRefreshToken(
                loginSessionCompleteRequest, authCode, httpServletResponse);

        return ResponseEntity.ok().build();
    }
}
