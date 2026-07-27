package com.sydneyarchive.item.service;

import com.sydneyarchive.common.enums.VisibilityStatus;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.exception.ItemException;
import com.sydneyarchive.item.dto.request.ItemCreateRequest;
import com.sydneyarchive.item.dto.request.ItemUpdateRequest;
import com.sydneyarchive.item.dto.response.ItemResponse;
import com.sydneyarchive.item.entity.Item;
import com.sydneyarchive.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    // TODO Pagination 적용
    public List<ItemResponse> findItems() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<Item> items = isAdmin
                ? itemRepository.findAll()
                : itemRepository.findByVisibilityStatus(VisibilityStatus.PUBLIC);

        return items.stream()
                .map(item -> ItemResponse.of(item, isAdmin))
                .toList();
    }

    public ItemResponse createItem(ItemCreateRequest request) {
        Item savedItem = itemRepository.save(Item.of(request));
        return ItemResponse.of(savedItem, true);
    }

    public ItemResponse updateItem(String itemId, ItemUpdateRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemException(ErrorCode.ITEM_NOT_FOUND));

        boolean isUpdated = item.update(request);
        if (isUpdated) {
            item = itemRepository.save(item);
        }

        return ItemResponse.of(item, true);
    }

    public void deleteItem(String itemId) {
        itemRepository.findById(itemId).ifPresent(itemRepository::delete);
    }
}
