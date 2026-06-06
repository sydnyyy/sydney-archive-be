package com.theforbiddenland.user.entity;

import com.theforbiddenland.auth.dto.internal.CustomOAuth2User;
import com.theforbiddenland.user.enums.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Document(collection = "users")
@Builder
@Getter
public class User {

    @Id
    private String id;

    private Role role;

    @Indexed(unique = true)
    private String sid;

    private String realName;

    private String username;

    private String provider;

    private String providerId;

    private String email;

    private String profileImageUrl;

    private Instant lastMessageAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Builder.Default
    private Set<String> likedItemIds = new LinkedHashSet<>();

    public static User of(Role role, String sid) {
        return User.builder()
                .role(role)
                .sid(sid)
                .build();
    }

    public static User of(CustomOAuth2User oauth2UserCustom, String sid, String username) {
        return User.builder()
                .role(oauth2UserCustom.getRole())
                .sid(sid)
                .realName(oauth2UserCustom.getRealName())
                .username(username)
                .provider(oauth2UserCustom.getProvider())
                .providerId(oauth2UserCustom.getProviderId())
                .email(oauth2UserCustom.getEmail())
                .build();
    }

    public void updateLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public void addLikedItemId(String itemId) {
        // 최신 설정
        if (this.likedItemIds.contains(itemId)) {
            this.likedItemIds.remove(itemId);
        }

        this.likedItemIds.add(itemId);
    }

    public void deleteLikeItemId(String itemId) {
        this.likedItemIds.remove(itemId);
    }
}
