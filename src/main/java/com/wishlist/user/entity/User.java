package com.wishlist.user.entity;

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
    private String clientId;

    private Instant lastMessageAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public static User of(Role role, String clientId) {
        return User.builder()
                .role(role)
                .clientId(clientId)
                .build();
    }

    public void updateLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
}
