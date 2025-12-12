package com.wishlist.item.service;

import com.wishlist.bookitem.dto.BookItemResponse;
import com.wishlist.bookitem.service.BookItemService;
import com.wishlist.item.dto.BookItemWithUserResponse;
import com.wishlist.user.dto.UserSummaryResponse;
import com.wishlist.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final BookItemService bookItemService;
    private final UserService userService;

    public List<BookItemWithUserResponse> findAllBookItemsWithUser() {
        List<BookItemResponse> responses = bookItemService.findAllBookItems();

        return responses.stream().map(bookItemResponse -> {
            UserSummaryResponse userSummaryResponse = userService.findUserSummary(bookItemResponse.userId());
            return BookItemWithUserResponse.of(bookItemResponse, userSummaryResponse);
        }).toList();
    }
}
