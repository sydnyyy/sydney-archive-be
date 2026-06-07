package com.theforbiddenland.item.dto.response;

import com.theforbiddenland.item.enums.ItemType;
import com.theforbiddenland.item.entity.Item;
import lombok.Builder;

import java.util.List;

@Builder
public record ItemResponse(

        String itemId,

        ItemType itemType,
        String title,
        String description,

        List<String> imageUrls,
        Integer thumbnailIndex,

        Permission permission
) {

    public static ItemResponse of(Item item, boolean isAdmin) {
        return ItemResponse.builder()
                .itemId(item.getId())
                .itemType(item.getItemType())
                .title(item.getTitle())
                .description(item.getDescription())
                .imageUrls(item.getImageUrls())
                .thumbnailIndex(item.getThumbnailIndex())
                .permission(Permission.of(isAdmin))
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
