package com.wishlist.item.api;

import com.wishlist.item.dto.BookItemWithUserResponse;
import com.wishlist.item.service.ItemService;
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
        List<BookItemWithUserResponse> responses = itemService.findAllBookItemsWithUser();
        return ResponseEntity.ok(responses);
    }
}
