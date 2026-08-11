package com.sydneyarchive.global.security.oauth2.service;

import com.sydneyarchive.auth.dto.internal.CustomOAuth2User;
import com.sydneyarchive.global.config.auth.AdminProperties;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AdminProperties adminProperties;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        CustomOAuth2User admin = extractOAuth2Profile(registrationId, attributes);
        if (!isAdmin(admin)) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(ErrorCode.NOT_AN_ADMIN.getCode()));
        }

        return admin;
    }

    private CustomOAuth2User extractOAuth2Profile(String providerId, Map<String, Object> attributes) {
        if (providerId.equals("naver")) {
            Map<String, Object> res = (Map<String, Object>) attributes.get("response");

            return CustomOAuth2User.builder()
                    .provider(providerId)
                    .providerId((String) res.get("id"))
                    .role(Role.ADMIN)
                    .email((String) res.get("email"))
                    .realName((String) res.get("name"))
                    .build();
        }

        throw new OAuth2AuthenticationException(
                new OAuth2Error(ErrorCode.UNSUPPORTED_PROVIDER.getCode()));
    }

    private boolean isAdmin(CustomOAuth2User customOAuth2User) {
        return Objects.equals(customOAuth2User.getEmail(), adminProperties.email());
    }
}
