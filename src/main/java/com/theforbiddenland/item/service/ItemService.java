package com.theforbiddenland.item.service;

import com.theforbiddenland.auth.security.UserPrincipal;
import com.theforbiddenland.item.dto.response.ItemResponse;
import com.theforbiddenland.item.repository.ItemRepository;
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

    private boolean isAdmin(UserPrincipal userPrincipal) {
        if (userPrincipal == null) return false;

        return userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_ADMIN"));
    }
}
