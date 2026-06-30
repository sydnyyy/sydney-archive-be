package com.sydneyarchive.item.service;

import com.sydneyarchive.auth.security.UserPrincipal;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.exception.ItemException;
import com.sydneyarchive.item.dto.request.ItemCreateRequest;
import com.sydneyarchive.item.dto.request.ItemUpdateRequest;
import com.sydneyarchive.item.dto.response.ItemResponse;
import com.sydneyarchive.item.entity.Item;
import com.sydneyarchive.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    // TODO Pagination 적용
    public List<ItemResponse> findItems(UserPrincipal userPrincipal) {
        boolean isAdmin = isAdmin(userPrincipal);

        return itemRepository.findAll()
                .stream()
                .map(i -> ItemResponse.of(i, isAdmin))
                .toList();
    }

    public ItemResponse createItem(ItemCreateRequest request, UserPrincipal userPrincipal) {
        if (!isAdmin(userPrincipal)) {
            throw new ItemException(ErrorCode.ACCESS_DENIED);
        }

        Item savedItem = itemRepository.save(Item.of(request, userPrincipal.getUserId()));
        return ItemResponse.of(savedItem, true);
    }

    public ItemResponse updateItem(String itemId, ItemUpdateRequest request, UserPrincipal userPrincipal) {
        if (!isAdmin(userPrincipal)) {
            throw new ItemException(ErrorCode.ACCESS_DENIED);
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemException(ErrorCode.ITEM_NOT_FOUND));

        boolean isUpdated = item.update(request);
        if (isUpdated) {
            item = itemRepository.save(item);
        }

        return ItemResponse.of(item, true);
    }

    public void deleteItem(String itemId, UserPrincipal userPrincipal) {
        if (!isAdmin(userPrincipal)) {
            throw new ItemException(ErrorCode.ACCESS_DENIED);
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemException(ErrorCode.ITEM_NOT_FOUND));

        if (!item.getAdminSid().equals(userPrincipal.getUserId())) {
            throw new ItemException(ErrorCode.ACCESS_DENIED);
        }

        itemRepository.delete(item);
    }

    private boolean isAdmin(UserPrincipal userPrincipal) {
        if (userPrincipal == null) return false;

        return userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_ADMIN"));
    }
}
