package com.forbiddenland.user.dto.internal;

import com.forbiddenland.user.entity.User;
import com.forbiddenland.user.enums.Role;
import lombok.Builder;

@Builder
public record UserAuthContext(
        String userId,
        Role role,
        boolean created
) {

    public static UserAuthContext of(User user, boolean created) {
        return UserAuthContext.builder()
                .userId(user.getId())
                .role(user.getRole())
                .created(created)
                .build();
    }
}
