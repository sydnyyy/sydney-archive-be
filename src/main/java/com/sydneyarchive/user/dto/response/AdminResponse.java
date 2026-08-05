package com.sydneyarchive.user.dto.response;

import com.sydneyarchive.user.entity.User;
import lombok.Builder;

@Builder
public record AdminResponse(
        String uid,
        String username,
        String email

) {
    public static AdminResponse of(User user) {
        return AdminResponse.builder()
                .uid(user.getUid())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
