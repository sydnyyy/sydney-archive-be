package com.sydneyarchive.auth.api;

import com.sydneyarchive.auth.dto.request.UserSidRequest;
import com.sydneyarchive.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserService userService;

    @PostMapping("/sid")
    private ResponseEntity<?> generateUserSid(
            @RequestBody(required = false) UserSidRequest userSidRequest
    ) {
        String sid = userService.saveUser(userSidRequest);
        return ResponseEntity.ok(Map.of("sid", sid));
    }
}
