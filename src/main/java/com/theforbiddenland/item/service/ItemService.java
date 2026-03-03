package com.theforbiddenland.item.service;

import com.theforbiddenland.bookitem.dto.BookItemResponse;
import com.theforbiddenland.bookitem.service.BookItemService;
import com.theforbiddenland.item.dto.ItemWithUserResponse;
import com.theforbiddenland.user.dto.UserSummaryResponse;
import com.theforbiddenland.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final BookItemService bookItemService;
    private final UserService userService;

    public List<ItemWithUserResponse> findAllItemsWithUser() {
        List<BookItemResponse> bookItemResponses = bookItemService.findAllBookItems();

        return bookItemResponses.stream().map(bookItemResponse -> {
            UserSummaryResponse userSummaryResponse = userService.findUserSummaryByUid(bookItemResponse.uid());
            return ItemWithUserResponse.of(bookItemResponse, userSummaryResponse);
        }).toList();
    }
}
