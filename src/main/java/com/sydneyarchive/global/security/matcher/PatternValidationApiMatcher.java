package com.sydneyarchive.global.security.matcher;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PatternValidationApiMatcher {

    private static final Set<String> STATE_MODIFYING_METHODS = Set.of("POST", "PUT", "DELETE");

    public boolean matches(HttpServletRequest request) {
        return isStateModifying(request) || isPersonalizedReadApi(request);
    }

    private boolean isStateModifying(HttpServletRequest request) {
        return STATE_MODIFYING_METHODS.contains(request.getMethod());
    }

    private boolean isPersonalizedReadApi(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.equals("/api/g/chat/messages")
                || uri.matches("^/api/g/likes/[^/]+$");
    }
}