package vn.hcmute.utetrainingpointssystem.model.auth;

/**
 * ForgotPasswordResponse - Response from forgot-password / reset-password endpoints
 * Backend always returns message: "If the email exists, a reset link has been sent."
 * or "Password updated successfully."
 */
public class ForgotPasswordResponse {
    public String message;

    // Legacy fields for backward compatibility
    public String email;
    public long resetId;
    public long expiresInSec;
    public String demoCode;
}
