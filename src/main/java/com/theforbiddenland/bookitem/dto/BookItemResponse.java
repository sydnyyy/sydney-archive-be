package com.theforbiddenland.bookitem.dto;

import com.theforbiddenland.bookitem.entity.BookItem;
import com.theforbiddenland.global.item.ItemType;
import lombok.Builder;

import java.util.List;

@Builder
public record BookItemResponse(

        String itemId,
        String uid,
        ItemType itemType,

        String title,
        String author,
        String description,

        List<String> imageUrls,
        Integer thumbnailIndex
) {

    public static BookItemResponse of(BookItem bookItem, String uid) {
        return BookItemResponse.builder()
                .itemId(bookItem.getId())
                .uid(uid)
                .itemType(bookItem.getItemType())
                .title(bookItem.getTitle())
                .author(bookItem.getAuthor())
                .description(bookItem.getDescription())
                .imageUrls(bookItem.getImageUrls())
                .thumbnailIndex(bookItem.getThumbnailIndex())
                .build();
    }
}
