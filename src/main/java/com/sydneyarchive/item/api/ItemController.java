package com.sydneyarchive.item.api;

import com.sydneyarchive.item.dto.request.ItemCreateRequest;
import com.sydneyarchive.item.dto.request.ItemUpdateRequest;
import com.sydneyarchive.item.dto.response.ItemResponse;
import com.sydneyarchive.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/c/items")
    public ResponseEntity<?> findAllItems(Authentication authentication) {
        boolean isAdmin = authentication != null
                && authentication.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        List<ItemResponse> responses = itemService.findItems(isAdmin);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/a/items")
    public ResponseEntity<?> createItem(@RequestBody ItemCreateRequest request) {
        ItemResponse response = itemService.createItem(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/a/items/{itemId}")
    public ResponseEntity<?> updateItem(
            @PathVariable String itemId,
            @RequestBody ItemUpdateRequest request
    ) {
        ItemResponse response = itemService.updateItem(itemId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/a/items/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable String itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.ok().build();
    }
}
