package com.sydneyarchive.item.api;

import com.sydneyarchive.item.dto.request.ItemCreateRequest;
import com.sydneyarchive.item.dto.request.ItemUpdateRequest;
import com.sydneyarchive.item.dto.response.ItemResponse;
import com.sydneyarchive.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<?> findAllItems() {
        List<ItemResponse> responses = itemService.findItems();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody ItemCreateRequest request) {
        ItemResponse response = itemService.createItem(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(
            @PathVariable String itemId,
            @RequestBody ItemUpdateRequest request
    ) {
        ItemResponse response = itemService.updateItem(itemId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable String itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.ok().build();
    }
}
