package com.theforbiddenland.item.service;

import com.theforbiddenland.item.dto.response.ItemResponse;
import com.theforbiddenland.item.repository.ItemRepository;
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
        return itemRepository.findAll()
                .stream().map(ItemResponse::of)
                .toList();
    }
}
