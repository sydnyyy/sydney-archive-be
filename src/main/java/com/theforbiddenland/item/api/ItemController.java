package com.theforbiddenland.item.api;

import com.theforbiddenland.item.dto.response.ItemResponse;
import com.theforbiddenland.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
