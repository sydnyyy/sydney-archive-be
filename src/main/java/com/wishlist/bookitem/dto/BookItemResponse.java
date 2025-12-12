package com.wishlist.bookitem.dto;

import com.wishlist.bookitem.entity.BookItem;
import com.wishlist.global.item.ItemType;
import lombok.Builder;

import java.util.List;

@Builder
public record BookItemResponse(

        String itemId,
        String userId,
        ItemType itemType,

        String title,
        String author,
        String description,

        List<String> imageUrls,
        Integer thumbnailIndex
) {

    public static BookItemResponse of(BookItem bookItem) {
        return BookItemResponse.builder()
                .itemId(bookItem.getId())
                .userId(bookItem.getUserId())
                .itemType(bookItem.getItemType())
                .title(bookItem.getTitle())
                .author(bookItem.getAuthor())
                .description(bookItem.getDescription())
                .imageUrls(bookItem.getImageUrls())
                .thumbnailIndex(bookItem.getThumbnailIndex())
                .build();
    }
}
