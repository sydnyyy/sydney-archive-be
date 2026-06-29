package com.forbiddenland.item.dto.response;

import com.forbiddenland.common.enums.VisibilityStatus;
import com.forbiddenland.item.entity.Item;
import lombok.Builder;

import java.util.List;

@Builder
public record ItemResponse(

        String itemId,

        String title,
        String description,

        List<String> imageUrls,
        Integer thumbnailIndex,

        Permission permission,
        VisibilityStatus visibilityStatus
) {

    public static ItemResponse of(Item item, boolean isAdmin) {
        return ItemResponse.builder()
                .itemId(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .imageUrls(item.getImageUrls())
                .thumbnailIndex(item.getThumbnailIndex())
                .permission(Permission.of(isAdmin))
                .visibilityStatus(item.getVisibilityStatus())
                .build();
    }

    private record Permission(
            boolean canEdit,
            boolean canDelete
    ) {
        private static Permission of(boolean isAdmin) {
            return new Permission(isAdmin, isAdmin);
        }
    }
}
