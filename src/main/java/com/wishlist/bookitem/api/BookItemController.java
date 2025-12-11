package com.wishlist.bookitem.api;

import com.wishlist.bookitem.dto.BookItemCreateRequest;
import com.wishlist.bookitem.dto.BookItemResponse;
import com.wishlist.bookitem.service.BookItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-items")
public class BookItemController {

    private final BookItemService bookItemService;

    @PostMapping("/{userId}")
    public ResponseEntity<?> createBookItem(
            @RequestBody BookItemCreateRequest request,
            @PathVariable String userId
    ) {

        BookItemResponse result = bookItemService.createBookItem(request, userId);
        return ResponseEntity.ok(result);
    }
}
