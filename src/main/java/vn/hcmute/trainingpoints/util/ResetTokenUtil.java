package vn.hcmute.trainingpoints.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class ResetTokenUtil {

    private static final SecureRandom RNG = new SecureRandom();

    /**
     * Generate random URL-safe token (256-bit)
     * Suitable for sending via email link
     */
    public static String randomToken() {
        byte[] bytes = new byte[32]; // 256-bit
        RNG.nextBytes(bytes);
        // URL-safe token (no padding)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Hash token with SHA-256
     * Input: raw token + pepper
     * Output: hex string for DB storage
     */
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 hashing failed", e);
        }
    }
}

