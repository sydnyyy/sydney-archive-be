package com.sydneyarchive.auth.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sydneyarchive.auth.dto.internal.LoginSessionContext;
import com.sydneyarchive.auth.dto.request.LoginSessionCompleteRequest;
import com.sydneyarchive.auth.dto.response.LoginSessionResponse;
import com.sydneyarchive.auth.enums.Platform;
import com.sydneyarchive.common.applicationevent.dto.internal.LoginSessionTask;
import com.sydneyarchive.common.applicationevent.enums.EventType;
import com.sydneyarchive.common.applicationevent.service.ApplicationEventProducer;
import com.sydneyarchive.common.util.HashUtils;
import com.sydneyarchive.global.config.auth.LoginSessionProperties;
import com.sydneyarchive.global.exception.ErrorCode;
import com.sydneyarchive.global.exception.LoginSessionException;
import com.sydneyarchive.global.exception.UserException;
import com.sydneyarchive.user.dto.internal.UserAuthContext;
import com.sydneyarchive.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import static com.sydneyarchive.auth.service.LoginSessionRedisService.LOGIN_SESSION_SECRET_HASH_FIELD_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginSessionService {

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    private final LoginSessionRedisService loginSessionRedisService;
    private final LoginSessionProperties loginSessionProperties;
    private final UserService userService;
    private final TokenService tokenService;
    private final ApplicationEventProducer applicationEventProducer;
    private final HashUtils hashUtils;

    public LoginSessionResponse generateLoginSession(String secretHash) {
        String sid = NanoIdUtils.randomNanoId();
        loginSessionRedisService.saveLoginSession(sid, secretHash);

        String qrCodeBase64 = generateQrCodeBase64(sid);

        long expiredAt = Instant.now()
                .plusSeconds(loginSessionProperties.getLoginSessionTimeoutSec())
                .toEpochMilli();

        return LoginSessionResponse.of(qrCodeBase64, sid, expiredAt);
    }

    private String generateQrCodeBase64(String sid) {
        int width = 250;
        int height = 250;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    frontendBaseUrl + "/login/qr/bridge?sid=" + sid,
                    BarcodeFormat.QR_CODE,
                    width, height);

            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);

            byte[] imageBytes = out.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (WriterException | IOException e) {
            log.error("QR 생성 실패 [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    public boolean isAvailableLoginSession(String sid) {
        return loginSessionRedisService.isAvailableLoginSession(sid);
    }

    public boolean bindStateAndPlatformToLoginSession(String sid, String state, Platform platform) {
        return loginSessionRedisService.bindStateAndPlatformToLoginSession(sid, state, platform);
    }

    public boolean assignUserIdToLoginSession(String state, String userId) {
        if (state == null || userId == null) return false;

        boolean isAssigned = loginSessionRedisService.assignUserIdToLoginSession(state, userId);
        if (!isAssigned) {
            log.warn("Failed to assign userId to auth session: no session mapping found for state. state={}", state);
        }

        return isAssigned;
    }

    public void verifyLoginSessionAndIssueRefreshToken(
            LoginSessionCompleteRequest loginSessionCompleteRequest,
            HttpServletResponse httpServletResponse
    ) {
        String storedSecretHash = loginSessionRedisService.getLoginSessionField(
                loginSessionCompleteRequest.sid(), LOGIN_SESSION_SECRET_HASH_FIELD_NAME
        );

        if (!hashUtils.verify(loginSessionCompleteRequest.secret(), storedSecretHash)) {
            throw new LoginSessionException(ErrorCode.LOGIN_SESSION_SECRET_MISMATCH);
        }

        String userId = loginSessionRedisService.verifySessionAndGetUserId(
                loginSessionCompleteRequest.sid(),
                loginSessionCompleteRequest.version()
        );

        try {
            UserAuthContext userAuthContext = userService.getUserContextById(userId);
            tokenService.issueRefreshTokenToCookie(userAuthContext.userId(), userAuthContext.role(), httpServletResponse);
        } catch (UserException e) {
            log.error("[Data Consistency Error] Redis session exists but User not found in DB (userId={})", userId);
            throw new LoginSessionException(e.getErrorCode());
        }
    }

    public Long getLoginSessionVersion(String sid) {
        return loginSessionRedisService.getLoginSessionVersion(sid);
    }

    public LoginSessionContext getLoginSessionContext(String state) {
        try {
            Map<String, Object> entries = loginSessionRedisService.getLoginSessionEntries(state);
            return LoginSessionContext.of(entries);
        } catch (LoginSessionException e) {
            log.warn("Failed to retrieve login session context for state={}", state, e);
            throw e;
        }
    }

    public void terminateLoginSession(String sid) {
        applicationEventProducer.publishEvent(
                LoginSessionTask.of(EventType.LOGIN_SESSION_DELETE, sid));
    }

    public void deleteLoginSession(String sid) {
        loginSessionRedisService.deleteLoingSession(sid);
    }
}
