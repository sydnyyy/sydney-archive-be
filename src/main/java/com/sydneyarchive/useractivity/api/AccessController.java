package com.sydneyarchive.useractivity.api;

import com.sydneyarchive.auth.security.UserPrincipal;
import com.sydneyarchive.useractivity.service.AccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/g/access")
@RequiredArgsConstructor
public class AccessController {

    private final AccessService accessService;

    @PostMapping("/{itemId}")
    public ResponseEntity<Void> recordAccess(
            @PathVariable String itemId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        accessService.recordAccess(userPrincipal.getUserId(), itemId);
        return ResponseEntity.ok().build();
    }
}