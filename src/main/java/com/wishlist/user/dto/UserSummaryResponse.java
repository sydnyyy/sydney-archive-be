package com.wishlist.user.dto;

import com.wishlist.user.entity.User;
import lombok.Builder;

import java.util.Optional;

@Builder
public record UserSummaryResponse (
        String uid,
        String displayName,
        String profileImageUrl
) {

    public static UserSummaryResponse of(User user) {
        return UserSummaryResponse.builder()
                .uid(user.getUid())
                .displayName(user.getDisplayName())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public static UserSummaryResponse ofOrUnknown(Optional<User> userOptional) {
        if (userOptional.isEmpty()) {
            return UserSummaryResponse.builder()
                    .uid("unknown")
                    .displayName("알 수 없는 사용자")
                    .profileImageUrl(null)
                    .build();
        }

        return UserSummaryResponse.builder()
                .uid(userOptional.get().getUid())
                .displayName(userOptional.get().getDisplayName())
                .profileImageUrl(userOptional.get().getProfileImageUrl())
                .build();
    }
}
