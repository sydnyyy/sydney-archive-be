package com.theforbiddenland.item.service;

import com.theforbiddenland.item.dto.response.ItemResponse;
import com.theforbiddenland.item.entity.Item;
import com.theforbiddenland.item.repository.ItemRepository;
import com.theforbiddenland.user.dto.UserSummaryResponse;
import com.theforbiddenland.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    // TODO Pagination 적용
    public List<ItemResponse> findItems() {
        List<Item> items = itemRepository.findAll();

        return items.stream().map(item -> {
            UserSummaryResponse userSummaryResponse = userService.findUserSummaryBySid(item.getOwnerSid());
            return ItemResponse.of(item, userSummaryResponse);
        }).toList();
    }
}
