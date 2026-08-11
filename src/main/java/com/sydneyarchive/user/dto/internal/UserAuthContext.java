package com.sydneyarchive.user.dto.internal;

import com.sydneyarchive.user.entity.User;
import com.sydneyarchive.user.enums.Role;
import lombok.Builder;

@Builder
public record UserAuthContext(
        String userId,
        String uid,
        Role role,
        boolean created
) {

    public static UserAuthContext of(User user, boolean created) {
        return UserAuthContext.builder()
                .userId(user.getId())
                .uid(user.getUid())
                .role(user.getRole())
                .created(created)
                .build();
    }
}
