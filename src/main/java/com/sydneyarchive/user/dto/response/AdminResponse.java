package com.sydneyarchive.user.dto.response;

import com.sydneyarchive.user.entity.User;
import lombok.Builder;

@Builder
public record AdminResponse(
        String username,
        String email

) {
    public static AdminResponse of(User user) {
        return AdminResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
