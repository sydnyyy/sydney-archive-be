package com.sydneyarchive.user.api;

import com.sydneyarchive.auth.security.UserPrincipal;
import com.sydneyarchive.user.dto.response.UserResponse;
import com.sydneyarchive.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/a/users/me")
    public ResponseEntity<?> getCurrentAdmin(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserResponse response = userService.getUserInfo(userPrincipal.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/g/users/me")
    public ResponseEntity<?> getCurrentGuest(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserResponse response = userService.getUserInfo(userPrincipal.getUserId());
        return ResponseEntity.ok(response);
    }
}
