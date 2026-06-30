package com.sydneyarchive.useractivity.api;

import com.sydneyarchive.useractivity.entity.AccessEvent;
import com.sydneyarchive.useractivity.service.AccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
public class AccessController {

    private final AccessService accessService;

    @PostMapping
    public ResponseEntity<Void> recordAccess(@RequestBody AccessEvent accessEvent) {
        accessService.recordAccess(accessEvent);
        return ResponseEntity.ok().build();
    }
}