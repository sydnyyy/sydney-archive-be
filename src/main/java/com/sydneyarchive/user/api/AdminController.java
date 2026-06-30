package com.sydneyarchive.user.api;

import com.sydneyarchive.auth.security.UserPrincipal;
import com.sydneyarchive.user.dto.response.AdminResponse;
import com.sydneyarchive.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentAdmin(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        AdminResponse response = userService.getAdmin(userPrincipal.getUserId());
        return ResponseEntity.ok(response);
    }
}
