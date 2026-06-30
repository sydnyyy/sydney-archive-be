package com.sydneyarchive.item.dto.request;

import com.sydneyarchive.common.enums.VisibilityStatus;

import java.util.List;

public record ItemUpdateRequest(
        String title,
        String description,
        List<String> imageUrls,
        Integer thumbnailIndex,
        VisibilityStatus visibilityStatus
) {
}
