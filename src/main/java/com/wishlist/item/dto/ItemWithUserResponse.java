package com.wishlist.item.dto;

import com.wishlist.bookitem.dto.BookItemResponse;
import com.wishlist.global.item.ItemType;
import com.wishlist.user.dto.UserSummaryResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record ItemWithUserResponse(
        String itemId,
        ItemType itemType,
        String title,
        String description,
        List<String> imageUrls,
        Integer thumbnailIndex,

        // book item only
        String author,

        String uid,
        String displayName,
        String profileImageUrl
) {

    public static ItemWithUserResponse of(BookItemResponse bookItemResponse, UserSummaryResponse userSummaryResponse) {
        return ItemWithUserResponse.builder()
                .itemId(bookItemResponse.itemId())
                .itemType(bookItemResponse.itemType())
                .title(bookItemResponse.title())
                .author(bookItemResponse.author())
                .description(bookItemResponse.description())
                .imageUrls(bookItemResponse.imageUrls())
                .thumbnailIndex(bookItemResponse.thumbnailIndex())
                .uid(userSummaryResponse.uid())
                .displayName(userSummaryResponse.displayName())
                .profileImageUrl(userSummaryResponse.profileImageUrl())
                .build();
    }
}
