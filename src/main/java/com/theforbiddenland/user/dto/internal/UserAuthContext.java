package com.theforbiddenland.user.dto.internal;

import com.theforbiddenland.user.entity.User;
import com.theforbiddenland.user.enums.Role;
import lombok.Builder;

@Builder
public record UserAuthContext(
        String userId,
        Role role
) {

    public static UserAuthContext of(User user) {
        return UserAuthContext.builder()
                .userId(user.getId())
                .role(user.getRole())
                .build();
    }
}
