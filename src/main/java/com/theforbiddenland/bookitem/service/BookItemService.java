package com.theforbiddenland.bookitem.service;

import com.theforbiddenland.bookitem.dto.BookItemCreateRequest;
import com.theforbiddenland.bookitem.dto.BookItemResponse;
import com.theforbiddenland.bookitem.entity.BookItem;
import com.theforbiddenland.bookitem.repository.BookItemRepository;
import com.theforbiddenland.user.service.UserService;
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
