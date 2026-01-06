package vn.hcmute.utetrainingpointssystem.model.auth;

public class ForgotPasswordResponse {
    public String email;
    public long resetId;
    public long expiresInSec;
    public String demoCode;
}
