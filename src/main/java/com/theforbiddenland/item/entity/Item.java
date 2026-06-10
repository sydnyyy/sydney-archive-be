package com.theforbiddenland.item.entity;

import com.theforbiddenland.common.enums.VisibilityStatus;
import com.theforbiddenland.item.dto.request.ItemCreateRequest;
import com.theforbiddenland.item.dto.request.ItemUpdateRequest;
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

    private String adminSid;

    private String title;
    private String description;

    private List<String> imageUrls;
    private Integer thumbnailIndex;

    private VisibilityStatus visibilityStatus;

    public static Item of(ItemCreateRequest request, String adminSid) {
        return Item.builder()
                .adminSid(adminSid)
                .title(request.title())
                .description(request.description())
                .imageUrls(request.imageUrls())
                .thumbnailIndex(request.thumbnailIndex())
                .visibilityStatus(request.visibilityStatus())
                .build();
    }

    public boolean update(ItemUpdateRequest request) {
        boolean isUpdated = false;

        if (request.title() != null) {
            this.title = request.title();
            isUpdated = true;
        }
        if (request.description() != null) {
            this.description = request.description();
            isUpdated = true;
        }
        if (request.imageUrls() != null) {
            this.imageUrls = request.imageUrls();
            isUpdated = true;
        }
        if (request.thumbnailIndex() != null) {
            this.thumbnailIndex = request.thumbnailIndex();
            isUpdated = true;
        }

        return isUpdated;
    }
}
