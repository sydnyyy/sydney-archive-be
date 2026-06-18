package com.theforbiddenland.auth.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.theforbiddenland.auth.dto.internal.AuthSessionContext;
import com.theforbiddenland.global.config.auth.AuthSessionProperties;
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
public class AuthSessionService {

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    private final AuthSessionRedisService authSessionRedisService;
    private final AuthSessionProperties authSessionProperties;

    public AuthSessionContext generateAuthSession() {
        String sid = UUID.randomUUID().toString();
        String authCode = UUID.randomUUID().toString();
        authSessionRedisService.saveAuthSession(sid, authCode);

        String qrCodeBase64 = generateQrCodeBase64(sid);

        long expiredAt = Instant.now()
                .plusSeconds(authSessionProperties.getAuthSessionTimeoutSec())
                .toEpochMilli();

        return AuthSessionContext.of(qrCodeBase64, sid, authCode, expiredAt);
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

    public boolean bindStateToAuthSession(String sid, String state) {
        return authSessionRedisService.bindStateToAuthSession(sid, state);
    }
}
