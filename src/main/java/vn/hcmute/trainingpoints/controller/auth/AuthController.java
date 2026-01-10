package vn.hcmute.trainingpoints.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.trainingpoints.dto.auth.*;
import vn.hcmute.trainingpoints.service.user.AuthService;
import vn.hcmute.trainingpoints.service.user.PasswordResetService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(@RequestBody RefreshTokenRequest req) {
        return authService.refreshToken(req.getRefreshToken());
    }

    /**
     * Request password reset link via email
     * Always returns 200 OK (does not reveal email existence)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<SimpleMessageResponse> forgotPassword(
            @RequestBody ForgotPasswordRequest req,
            HttpServletRequest http
    ) {
        String ip = http.getRemoteAddr();
        String userAgent = http.getHeader("User-Agent");
        passwordResetService.requestPasswordReset(req.getEmail(), ip, userAgent);

        return ResponseEntity.ok(SimpleMessageResponse.builder()
                .message("If the email exists, a reset link has been sent.")
                .build());
    }

    /**
     * Reset password with token from email link
     */
    @PostMapping("/reset-password")
    public ResponseEntity<SimpleMessageResponse> resetPassword(
            @RequestBody ResetPasswordRequest req
    ) {
        passwordResetService.resetPassword(req.getToken(), req.getNewPassword());

        return ResponseEntity.ok(SimpleMessageResponse.builder()
                .message("Password updated successfully.")
                .build());
    }
}
