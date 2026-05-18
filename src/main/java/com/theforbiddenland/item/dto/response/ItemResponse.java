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
        Integer thumbnailIndex
) {

    public static ItemResponse of(Item item) {
        return ItemResponse.builder()
                .itemId(item.getId())
                .itemType(item.getItemType())
                .title(item.getTitle())
                .description(item.getDescription())
                .imageUrls(item.getImageUrls())
                .thumbnailIndex(item.getThumbnailIndex())
                .build();
    }
}
