package com.sydneyarchive.user.api;

import com.sydneyarchive.user.dto.request.UidRequest;
import com.sydneyarchive.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/uid")
    private ResponseEntity<?> generateUid(
            @RequestBody(required = false) UidRequest uidRequest
    ) {
        String uid = userService.saveUser(uidRequest);
        return ResponseEntity.ok(Map.of("uid", uid));
    }
}
