package vn.hcmute.trainingpoints.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.hcmute.trainingpoints.entity.user.PasswordResetToken;
import vn.hcmute.trainingpoints.entity.user.User;
import vn.hcmute.trainingpoints.repository.user.PasswordResetTokenRepository;
import vn.hcmute.trainingpoints.repository.user.UserRepository;
import vn.hcmute.trainingpoints.service.email.ResendEmailService;
import vn.hcmute.trainingpoints.util.ResetTokenUtil;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final ResendEmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.reset.token.expiry-minutes:15}")
    private long expiryMinutes;

    @Value("${app.reset.pepper}")
    private String pepper;

    @Value("${app.reset.frontend-url}")
    private String frontendResetUrl;

    /**
     * Step 1: Request password reset link
     * Always returns 200 OK (does not reveal if email exists)
     *
     * @param email user email
     * @param ip client IP address (for audit)
     * @param userAgent client user agent (for audit)
     */
    @Transactional
    public void requestPasswordReset(String email, String ip, String userAgent) {
        if (email == null || email.isBlank()) {
            return;
        }

        String normalizedEmail = email.trim();

        // Step 1: Check if user exists
        var userOpt = userRepository.findByEmail(normalizedEmail);

        if (userOpt.isEmpty()) {
            log.warn("Password reset requested for an unknown email address");
            return; // Silent fail (don't reveal email existence)
        }

        var user = userOpt.get();

        // Step 2: Check if account is active
        if (user.getStatus() != null && !user.getStatus()) {
            log.warn("Password reset attempted for a disabled account");
            return; // Silent fail
        }

        // Step 3: Generate token
        String rawToken = ResetTokenUtil.randomToken();
        String tokenHash = ResetTokenUtil.sha256(rawToken + pepper);

        // Step 4: Invalidate every older unused token for this user
        tokenRepository.deleteByUserIdAndUsedAtIsNull(user.getId());

        // Step 5: Save token hash (not raw token). Persistence errors must roll back.
        PasswordResetToken token = PasswordResetToken.builder()
                .userId(user.getId())
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusMinutes(expiryMinutes))
                .requestIp(ip)
                .userAgent(userAgent)
                .createdAt(LocalDateTime.now())
                .build();

        tokenRepository.save(token);

        // Step 6: Build reset URL
        String resetUrl = frontendResetUrl + "?token=" + rawToken;

        // Step 7: Email delivery errors remain non-enumerating, but DB errors are not hidden.
        try {
            emailService.sendResetLink(user.getEmail(), resetUrl);
        } catch (Exception e) {
            log.error("Password reset email delivery failed", e);
        }
    }

    /**
     * Step 2: Reset password with token
     * Validates token, updates password, invalidates token
     *
     * @param rawToken token from email link
     * @param newPassword new password
     */
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        if (rawToken == null || rawToken.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Token is required");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "New password is required");
        }

        if (newPassword.length() < 8 || newPassword.length() > 72) {
            throw new ResponseStatusException(BAD_REQUEST, "Password must be between 8 and 72 characters");
        }

        // Step 1: Hash the token to find it in DB
        String tokenHash = ResetTokenUtil.sha256(rawToken.trim() + pepper);

        // Step 2: Find valid (unused & not expired) token
        var tokenOpt = tokenRepository.findTopByTokenHashAndUsedAtIsNullAndExpiresAtAfter(
                tokenHash,
                LocalDateTime.now()
        );

        if (tokenOpt.isEmpty()) {
            log.warn("Password reset attempted with invalid or expired token");
            throw new ResponseStatusException(BAD_REQUEST, "Invalid or expired token");
        }

        PasswordResetToken token = tokenOpt.get();

        // Step 3: Find user
        var userOpt = userRepository.findById(token.getUserId());
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "User not found");
        }

        User user = userOpt.get();

        // Step 4: Verify new password is different from current (optional but recommended)
        // Note: You can remove this check if you want to allow setting the same password
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(BAD_REQUEST, "New password must be different from current password");
        }

        try {
            // Step 5: Update password with BCrypt
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Step 6: Mark token as used
            token.setUsedAt(LocalDateTime.now());
            tokenRepository.save(token);

            log.info("Password reset successfully for user ID: {}", user.getId());
        } catch (Exception e) {
            log.error("Error resetting password", e);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Failed to reset password");
        }
    }
}

