package com.theforbiddenland.item.api;

import com.theforbiddenland.auth.security.UserPrincipal;
import com.theforbiddenland.item.dto.response.ItemResponse;
import com.theforbiddenland.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<?> findAllItems(
            @AuthenticationPrincipal(expression = " #this == 'anonymousUser' ? null : #this ") UserPrincipal userPrincipal
    ) {
        List<ItemResponse> responses = itemService.findItems(userPrincipal);
        return ResponseEntity.ok(responses);
    }
}
