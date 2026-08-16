package com.sydneyarchive.user.dto.response;

import com.sydneyarchive.user.entity.User;
import lombok.Builder;

@Builder
public record UserResponse(
        String userId

) {
    public static UserResponse of(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .build();
    }
}
