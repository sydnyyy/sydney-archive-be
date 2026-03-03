package com.theforbiddenland.auth.dto;

import com.theforbiddenland.user.enums.Role;
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
