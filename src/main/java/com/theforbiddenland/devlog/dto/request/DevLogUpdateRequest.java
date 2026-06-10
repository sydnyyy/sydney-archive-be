package com.theforbiddenland.devlog.dto.request;

import com.theforbiddenland.common.enums.VisibilityStatus;

public record DevLogUpdateRequest(
        String title,
        String description,
        VisibilityStatus visibilityStatus
) {
}
