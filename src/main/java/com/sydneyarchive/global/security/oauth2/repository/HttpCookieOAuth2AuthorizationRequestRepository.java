package com.sydneyarchive.global.security.oauth2.repository;

import com.sydneyarchive.global.cookie.CookieUtils;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.security.oauth2.util.AesGcmUtils;
import com.sydneyarchive.global.security.oauth2.util.GzipUtils;
import com.sydneyarchive.global.security.oauth2.util.SerializationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import static com.sydneyarchive.global.cookie.CookieUtils.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final CookieUtils cookieUtils;
    private final AesGcmUtils aesGcmUtils;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        try {
            return decryptAndDeserialize(
                    cookieUtils.getCookie(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, request),
                    OAuth2AuthorizationRequest.class
            );
        } catch (Exception e) {
            log.error("Failed to decrypt and deserialize OAuth2AuthorizationRequest. request={} {}",
                    request.getMethod(), request.getRequestURI()
            );
            throw new OAuth2AuthenticationException(ErrorCode.LOGIN_PROCESSING_FAILED.getCode());
        }
    }

    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            String encryptedData = serializeAndEncode(authorizationRequest);
            cookieUtils.setCookie(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, encryptedData, response);
        } catch (Exception e) {
            log.error("Failed to serialize and encrypt OAuth2AuthorizationRequest.");
            throw e;
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            return this.loadAuthorizationRequest(request);
        } finally {
            cookieUtils.removeCookie(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, response);
        }
    }

    private String serializeAndEncode(OAuth2AuthorizationRequest authorizationRequest) {
        try {
            byte[] serializedBytes = SerializationUtils.serialize(authorizationRequest);
            byte[] compressedBytes = GzipUtils.compress(serializedBytes);
            return aesGcmUtils.encrypt(compressedBytes);
        } catch (Exception e) {
            throw new RuntimeException("encryption failed");
        }
    }

    private <T> T decryptAndDeserialize(String encryptedText, Class<T> clazz) {
        try {
            byte[] decryptedBytes = aesGcmUtils.decrypt(encryptedText);
            byte[] decompressedBytes = GzipUtils.decompress(decryptedBytes);
            Object object = SerializationUtils.deserialize(decompressedBytes);
            return clazz.cast(object);
        } catch (Exception e) {
            throw new RuntimeException("decryption failed");
        }
    }
}
