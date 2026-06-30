package com.sydneyarchive.item.api;

import com.sydneyarchive.auth.security.UserPrincipal;
import com.sydneyarchive.item.dto.request.ItemCreateRequest;
import com.sydneyarchive.item.dto.request.ItemUpdateRequest;
import com.sydneyarchive.item.dto.response.ItemResponse;
import com.sydneyarchive.item.service.ItemService;
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

    @PostMapping
    public ResponseEntity<?> createItem(
            @RequestBody ItemCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        ItemResponse response = itemService.createItem(request, userPrincipal);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(
            @PathVariable String itemId,
            @RequestBody ItemUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        ItemResponse response = itemService.updateItem(itemId, request, userPrincipal);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable String itemId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        itemService.deleteItem(itemId, userPrincipal);
        return ResponseEntity.ok().build();
    }
}
