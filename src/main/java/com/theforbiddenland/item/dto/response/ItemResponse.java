package com.theforbiddenland.item.dto.response;

import com.theforbiddenland.common.entity.Permission;
import com.theforbiddenland.common.enums.VisibilityStatus;
import com.theforbiddenland.item.entity.Item;
import lombok.Builder;

import java.util.List;

@Builder
public record ItemResponse(

        String itemId,

        String title,
        String description,

        List<String> imageUrls,
        Integer thumbnailIndex,

        VisibilityStatus visibilityStatus,
        Permission permission
) {

    public static ItemResponse of(Item item, boolean isAdmin) {
        return ItemResponse.builder()
                .itemId(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .imageUrls(item.getImageUrls())
                .thumbnailIndex(item.getThumbnailIndex())
                .visibilityStatus(item.getVisibilityStatus())
                .permission(Permission.of(isAdmin))
                .build();
    }
}
