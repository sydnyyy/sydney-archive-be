package com.theforbiddenland.devlog.dto.response;

import com.theforbiddenland.devlog.entity.DevLog;
import com.theforbiddenland.common.entity.Permission;
import com.theforbiddenland.common.enums.VisibilityStatus;
import lombok.Builder;

@Builder
public record DevLogResponse(
        String id,

        String title,
        String description,

        VisibilityStatus visibilityStatus,
        Permission permission
) {

    public static DevLogResponse of(DevLog devLog, boolean isAdmin) {
        return DevLogResponse.builder()
                .id(devLog.getId())
                .title(devLog.getTitle())
                .description(devLog.getDescription())
                .visibilityStatus(devLog.getVisibilityStatus())
                .permission(Permission.of(isAdmin))
                .build();
    }
}
