package com.theforbiddenland.auth.api;

import com.theforbiddenland.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class GuestAuthController {

    private final UserService userService;

    @PostMapping("/sid")
    private ResponseEntity<?> generateGuestSid() {
        String sid = userService.saveGuest();
        return ResponseEntity.ok(Map.of("sid", sid));
    }
}
