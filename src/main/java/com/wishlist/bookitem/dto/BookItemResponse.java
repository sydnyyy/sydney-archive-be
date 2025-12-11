package com.wishlist.bookitem.dto;

import com.wishlist.bookitem.entity.BookItem;
import lombok.Builder;

import java.util.List;

@Builder
public record BookItemResponse(

        String id,
        String title,
        String author,
        String description,
        List<String> imageUrls,
        Integer thumbnailIndex
) {

    public static BookItemResponse of(BookItem bookItem) {
        return BookItemResponse.builder()
                .id(bookItem.getId())
                .title(bookItem.getTitle())
                .author(bookItem.getAuthor())
                .description(bookItem.getDescription())
                .imageUrls(bookItem.getImageUrls())
                .thumbnailIndex(bookItem.getThumbnailIndex())
                .build();
    }
}
