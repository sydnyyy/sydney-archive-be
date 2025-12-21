package com.wishlist.user.entity;

import com.wishlist.auth.dto.OAuth2Profile;
import com.wishlist.user.enums.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
@Builder
@Getter
public class User {

    @Id
    private String id;

    private Role role;

    @Indexed(unique = true)
    private String uid;

    private String realName;

    private String displayName;

    private String provider;

    private String providerId;

    private String email;

    private String mobileNumber;

    private String profileImageUrl;

    private Instant lastMessageAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public static User of(Role role, String uid) {
        return User.builder()
                .role(role)
                .uid(uid)
                .build();
    }

    public static User of(OAuth2Profile oauth2Profile) {
        return User.builder()
                .role(oauth2Profile.role())
                .realName(oauth2Profile.realName())
                .provider(oauth2Profile.provider())
                .providerId(oauth2Profile.providerId())
                .email(oauth2Profile.email())
                .mobileNumber(oauth2Profile.mobileNumber())
                .build();
    }

    public void updateLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
}
