package com.forbiddenland.item.dto.request;

import com.forbiddenland.common.enums.VisibilityStatus;

import java.util.List;

public record ItemCreateRequest(
        String title,
        String description,
        List<String> imageUrls,
        Integer thumbnailIndex,
        VisibilityStatus visibilityStatus
) {
}
