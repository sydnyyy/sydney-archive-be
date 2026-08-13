package com.sydneyarchive.common.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class HashUtils {

    private static final String HASH_ALGORITHM = "SHA-256";

    public boolean verify(String secret, String storedSecretHash) {
        if (secret == null || storedSecretHash == null) {
            return false;
        }

        String computedHash = generateCodeChallenge(secret);

        return MessageDigest.isEqual(
                computedHash.getBytes(StandardCharsets.UTF_8),
                storedSecretHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String generateCodeChallenge(String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Hash algorithm is not available", e);
        }
    }
}
