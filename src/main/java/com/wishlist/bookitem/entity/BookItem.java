package com.wishlist.bookitem.entity;

import com.wishlist.bookitem.dto.BookItemCreateRequest;
import com.wishlist.global.item.ItemType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "book_items")
@Builder
@Getter
public class BookItem {

    @Id
    private String id;

    private String userId;

    private ItemType itemType;

    private String title;
    private String author;
    private String description;

    private List<String> imageUrls;
    private Integer thumbnailIndex;

    public static BookItem of(BookItemCreateRequest request, String userId) {
        return BookItem.builder()
                .userId(userId)
                .itemType(request.itemType())
                .title(request.title())
                .author(request.author())
                .description(request.description())
                .imageUrls(request.imageUrls())
                .thumbnailIndex(request.thumbnailIndex())
                .build();
    }
}
