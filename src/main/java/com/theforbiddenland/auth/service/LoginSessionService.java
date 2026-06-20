package com.theforbiddenland.auth.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.theforbiddenland.auth.dto.internal.LoginSessionContext;
import com.theforbiddenland.auth.dto.request.LoginSessionCompleteRequest;
import com.theforbiddenland.global.config.auth.LoginSessionProperties;
import com.theforbiddenland.global.exception.LoginSessionException;
import com.theforbiddenland.global.exception.UserException;
import com.theforbiddenland.user.dto.internal.UserAuthContext;
import com.theforbiddenland.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

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

    public LoginSessionContext generateLoginSession() {
        String sid = UUID.randomUUID().toString();
        String authCode = UUID.randomUUID().toString();
        loginSessionRedisService.saveLoginSession(sid, authCode);

        String qrCodeBase64 = generateQrCodeBase64(sid);

        long expiredAt = Instant.now()
                .plusSeconds(loginSessionProperties.getLoginSessionTimeoutSec())
                .toEpochMilli();

        return LoginSessionContext.of(qrCodeBase64, sid, authCode, expiredAt);
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

    public boolean bindStateToLoginSession(String sid, String state) {
        return loginSessionRedisService.bindStateToLoginSession(sid, state);
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
            String authCode,
            HttpServletResponse httpServletResponse
    ) {
        String userId = loginSessionRedisService.verifySessionAndGetUserId(
                loginSessionCompleteRequest.sid(), loginSessionCompleteRequest.version(), authCode);

        try {
            UserAuthContext userAuthContext = userService.getUserContext(userId);
            tokenService.issueRefreshTokenToCookie(userAuthContext.userId(), userAuthContext.role(), httpServletResponse);
        } catch (UserException e) {
            log.error("[Data Consistency Error] Redis session exists but User not found in DB (userId={})", userId);
            throw new LoginSessionException(e.getErrorCode());
        }
    }

    public Long getLoginSessionVersion(String sid) {
        return loginSessionRedisService.getLoginSessionVersion(sid);
    }

}
