package com.wishlist.bookitem.service;

import com.wishlist.bookitem.dto.BookItemCreateRequest;
import com.wishlist.bookitem.dto.BookItemResponse;
import com.wishlist.bookitem.entity.BookItem;
import com.wishlist.bookitem.repository.BookItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookItemService {

    private final BookItemRepository bookItemRepository;

    public BookItemResponse createBookItem(BookItemCreateRequest request, String userId) {
        BookItem bookItem = BookItem.of(request, userId);
        bookItemRepository.save(bookItem);
        return BookItemResponse.of(bookItem);
    }

    public List<BookItemResponse> findAllBookItems() {
        return bookItemRepository.findAll()
                .stream()
                .map(BookItemResponse::of)
                .toList();
    }
}
