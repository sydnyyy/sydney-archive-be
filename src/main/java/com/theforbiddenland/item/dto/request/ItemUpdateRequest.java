package com.theforbiddenland.item.dto.request;

import com.theforbiddenland.global.enums.VisibilityStatus;

import java.util.List;

public record ItemUpdateRequest(
        String title,
        String description,
        List<String> imageUrls,
        Integer thumbnailIndex,
        VisibilityStatus visibilityStatus
) {
}
