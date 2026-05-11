package com.theforbiddenland.user.dto;

import com.theforbiddenland.user.entity.User;
import lombok.Builder;

import java.util.Optional;

@Builder
public record UserSummaryResponse (
        String sid,
        String displayName,
        String profileImageUrl
) {

    public static UserSummaryResponse of(User user) {
        return UserSummaryResponse.builder()
                .sid(user.getSid())
                .displayName(user.getDisplayName())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public static UserSummaryResponse ofOrUnknown(Optional<User> userOptional) {
        if (userOptional.isEmpty()) {
            return UserSummaryResponse.builder()
                    .sid("unknown")
                    .displayName("알 수 없는 사용자")
                    .profileImageUrl(null)
                    .build();
        }

        return UserSummaryResponse.builder()
                .sid(userOptional.get().getSid())
                .displayName(userOptional.get().getDisplayName())
                .profileImageUrl(userOptional.get().getProfileImageUrl())
                .build();
    }
}
