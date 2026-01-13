package vn.hcmute.utetrainingpointssystem.model.auth;

/**
 * ResetPasswordRequest - Reset password with token from email link
 * Backend endpoint: POST /api/auth/reset-password
 */
public class ResetPasswordRequest {
    public String token;
    public String newPassword;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }
}
