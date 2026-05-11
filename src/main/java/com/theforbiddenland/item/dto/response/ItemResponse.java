package com.theforbiddenland.item.dto.response;

import com.theforbiddenland.item.enums.ItemType;
import com.theforbiddenland.item.entity.Item;
import com.theforbiddenland.user.dto.UserSummaryResponse;
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

        String ownerDisplayName,
        String ownerProfileImageUrl
) {

    public static ItemResponse of(Item item, UserSummaryResponse userSummaryResponse) {
        return ItemResponse.builder()
                .itemId(item.getId())
                .itemType(item.getItemType())
                .title(item.getTitle())
                .description(item.getDescription())
                .imageUrls(item.getImageUrls())
                .thumbnailIndex(item.getThumbnailIndex())
                .ownerDisplayName(userSummaryResponse.displayName())
                .ownerProfileImageUrl(userSummaryResponse.profileImageUrl())
                .build();
    }
}
