package com.wishlist.user.dto;

import com.wishlist.user.entity.User;
import lombok.Builder;

import java.util.Optional;

@Builder
public record UserSummaryResponse (
        String id,
        String displayName,
        String profileImageUrl
) {

    public static UserSummaryResponse of(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .displayName(user.getDisplayName())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public static UserSummaryResponse ofOrUnknown(Optional<User> userOptional) {
        if (userOptional.isEmpty()) {
            return UserSummaryResponse.builder()
                    .id("unknown")
                    .displayName("알 수 없는 사용자")
                    .profileImageUrl(null)
                    .build();
        }

        return UserSummaryResponse.builder()
                .id(userOptional.get().getId())
                .displayName(userOptional.get().getDisplayName())
                .profileImageUrl(userOptional.get().getProfileImageUrl())
                .build();
    }
}
