package com.theforbiddenland.item.entity;

import com.theforbiddenland.item.enums.ItemType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "items")
@Getter
@Builder
public class Item {

    @Id
    private String id;

    private String ownerSid;

    private ItemType itemType;
    private String title;
    private String description;

    private List<String> imageUrls;
    private Integer thumbnailIndex;
}
