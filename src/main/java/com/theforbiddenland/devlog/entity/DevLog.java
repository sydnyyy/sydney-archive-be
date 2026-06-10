package com.theforbiddenland.devlog.entity;

import com.theforbiddenland.devlog.dto.request.DevLogCreateRequest;
import com.theforbiddenland.devlog.dto.request.DevLogUpdateRequest;
import com.theforbiddenland.common.enums.VisibilityStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "devlogs")
@Builder
@Getter
public class DevLog {

    @Id
    private String id;

    private String adminId;

    private String title;
    private String description;

    private VisibilityStatus visibilityStatus;

    public static DevLog of(DevLogCreateRequest request, String adminId) {
        return DevLog.builder()
                .adminId(adminId)
                .title(request.title())
                .description(request.description())
                .visibilityStatus(request.visibilityStatus())
                .build();
    }

    public boolean update(DevLogUpdateRequest request) {
        boolean isUpdated = false;

        if (request.title() != null) {
            this.title = request.title();
            isUpdated = true;
        }
        if (request.description() != null) {
            this.description = request.description();
            isUpdated = true;
        }
        if (request.visibilityStatus() != null) {
            this.visibilityStatus = request.visibilityStatus();
        }

        return isUpdated;
    }
}
