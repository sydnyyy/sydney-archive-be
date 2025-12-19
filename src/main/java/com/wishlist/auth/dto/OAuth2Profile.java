package com.wishlist.auth.dto;

import com.wishlist.user.enums.Role;
import lombok.Builder;

@Builder
public record OAuth2Profile(
        String provider,
        String providerId,
        Role role,
        String email,
        String realName,
        String mobileNumber
) { }
