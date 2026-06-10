package com.theforbiddenland.devlog.api;

import com.theforbiddenland.auth.security.UserPrincipal;
import com.theforbiddenland.devlog.dto.request.DevLogCreateRequest;
import com.theforbiddenland.devlog.dto.request.DevLogUpdateRequest;
import com.theforbiddenland.devlog.dto.response.DevLogResponse;
import com.theforbiddenland.devlog.service.DevLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/devlogs")
public class DevLogController {

    private final DevLogService devLogService;

    @GetMapping
    private ResponseEntity<?> findDevLogs(
            @AuthenticationPrincipal(expression = " #this == 'anonymousUser' ? null : #this ") UserPrincipal userPrincipal
    ) {
        List<DevLogResponse> response = devLogService.findDevLogs(userPrincipal);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createDevLog(
            @RequestBody DevLogCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        DevLogResponse response = devLogService.createDevLog(request, userPrincipal);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{devLogId}")
    public ResponseEntity<?> updateDevLog(
            @PathVariable String devLogId,
            @RequestBody DevLogUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        DevLogResponse response = devLogService.updateDevLog(devLogId, request, userPrincipal);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{devLogId}")
    public ResponseEntity<?> deleteDevLog(
            @PathVariable String devLogId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        devLogService.deleteDevLog(devLogId, userPrincipal);
        return ResponseEntity.ok().build();
    }
}
