package com.forbiddenland.item.dto.request;

import com.forbiddenland.common.enums.VisibilityStatus;

import java.util.List;

public record ItemUpdateRequest(
        String title,
        String description,
        List<String> imageUrls,
        Integer thumbnailIndex,
        VisibilityStatus visibilityStatus
) {
}
