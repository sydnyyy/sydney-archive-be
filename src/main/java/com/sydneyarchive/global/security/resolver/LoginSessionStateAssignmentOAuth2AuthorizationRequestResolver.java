package com.sydneyarchive.global.security.resolver;

import com.sydneyarchive.auth.enums.Platform;
import com.sydneyarchive.auth.service.LoginSessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class LoginSessionStateAssignmentOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;
    private final LoginSessionService loginSessionService;

    public LoginSessionStateAssignmentOAuth2AuthorizationRequestResolver(
            ClientRegistrationRepository repository,
            LoginSessionService loginSessionService
    ) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repository, "/oauth2/authorization");
        this.loginSessionService = loginSessionService;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authRequest = defaultResolver.resolve(request);
        return customize(authRequest, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authRequest = defaultResolver.resolve(request, clientRegistrationId);
        return customize(authRequest, request);
    }

    private OAuth2AuthorizationRequest customize(
            OAuth2AuthorizationRequest authRequest,
            HttpServletRequest request
    ) {
        if (authRequest == null) return null;

        String sid = request.getParameter("sid");
        if (sid == null) return null;

        String authRequestState = authRequest.getState();
        Platform platform = Platform.fromString(request.getParameter("platform"));
        boolean isAssigned = loginSessionService.bindStateAndPlatformToLoginSession(sid, authRequestState, platform);
        if (!isAssigned) return null;

        return OAuth2AuthorizationRequest.from(authRequest)
                .build();
    }
}
