package com.theforbiddenland.global.resolver;

import com.theforbiddenland.global.annotation.UserIdFromUid;
import com.theforbiddenland.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserIdFromUidResolver implements HandlerMethodArgumentResolver {

    private final UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserIdFromUid.class)
                && parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        // PathVariable 추출 시도
        Map<String, String> uriVariables = (Map<String, String>) webRequest.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);

        String uid = (uriVariables != null) ? uriVariables.get("uid") : null;

        // RequestParam 추출 시도
        if (uid == null) {
            uid = webRequest.getParameter("uid");
        }

        if (uid == null) {
            throw new IllegalArgumentException("요청 경로(Path) 또는 파라미터(Query)에 uid가 누락되었습니다.");
        }

        return userService.findUserIdByUid(uid);
    }
}
