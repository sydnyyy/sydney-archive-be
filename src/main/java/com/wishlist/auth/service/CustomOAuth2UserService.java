package com.wishlist.auth.service;

import com.wishlist.auth.dto.OAuth2Profile;
import com.wishlist.user.enums.Role;
import com.wishlist.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2Profile oauth2Profile = extractOAuth2Profile(registrationId, attributes);
        userService.saveOrUpdate(oauth2Profile);

        return oAuth2User;
    }

    private OAuth2Profile extractOAuth2Profile(String providerId, Map<String, Object> attributes) {
        if (providerId.equals("naver")) {
            Map<String, Object> res = (Map<String, Object>) attributes.get("response");

            return OAuth2Profile.builder()
                    .role(Role.USER)
                    .provider(providerId)
                    .providerId((String) res.get("id"))
                    .email((String) res.get("email"))
                    .realName((String) res.get("name"))
                    .mobileNumber((String) res.get("mobile"))
                    .build();
        }

        throw new IllegalArgumentException("Unsupported provider: " + providerId);

    }
}
