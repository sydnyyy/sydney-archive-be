package com.theforbiddenland.item.dto.request;

import com.theforbiddenland.item.enums.ItemType;

import java.util.List;

public record ItemUpdateRequest(
        ItemType itemType,
        String title,
        String description,
        List<String> imageUrls,
        Integer thumbnailIndex
) {
}
