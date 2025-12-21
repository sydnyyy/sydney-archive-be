package com.wishlist.bookitem.service;

import com.wishlist.bookitem.dto.BookItemCreateRequest;
import com.wishlist.bookitem.dto.BookItemResponse;
import com.wishlist.bookitem.entity.BookItem;
import com.wishlist.bookitem.repository.BookItemRepository;
import com.wishlist.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookItemService {

    private final BookItemRepository bookItemRepository;
    private final UserService userService;

    public BookItemResponse createBookItem(BookItemCreateRequest request, String userId) {
        BookItem bookItem = BookItem.of(request, userId);
        bookItemRepository.save(bookItem);

        String uid = userService.findUidByUserId(userId);
        return BookItemResponse.of(bookItem, uid);
    }

    public List<BookItemResponse> findAllBookItems() {
        return bookItemRepository.findAll()
                .stream()
                .map(bookItem -> {
                    String uid = userService.findUidByUserId(bookItem.getUserId());
                    return BookItemResponse.of(bookItem, uid);
                })
                .toList();
    }
}
