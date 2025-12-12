package com.wishlist.item.dto;

import com.wishlist.bookitem.dto.BookItemResponse;
import com.wishlist.global.item.ItemType;
import com.wishlist.user.dto.UserSummaryResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record BookItemWithUserResponse(
        String itemId,
        ItemType itemType,
        String title,
        String author,
        String description,
        List<String> imageUrls,
        Integer thumbnailIndex,

        String userId,
        String displayName,
        String profileImageUrl
) {

    public static BookItemWithUserResponse of(BookItemResponse bookItemResponse, UserSummaryResponse userSummaryResponse) {
        return BookItemWithUserResponse.builder()
                .itemId(bookItemResponse.itemId())
                .itemType(bookItemResponse.itemType())
                .title(bookItemResponse.title())
                .author(bookItemResponse.author())
                .description(bookItemResponse.description())
                .imageUrls(bookItemResponse.imageUrls())
                .thumbnailIndex(bookItemResponse.thumbnailIndex())
                .userId(userSummaryResponse.id())
                .displayName(userSummaryResponse.displayName())
                .profileImageUrl(userSummaryResponse.profileImageUrl())
                .build();
    }
}
