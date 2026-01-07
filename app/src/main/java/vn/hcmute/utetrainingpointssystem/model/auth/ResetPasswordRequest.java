package vn.hcmute.utetrainingpointssystem.model.auth;

public class ResetPasswordRequest {
    public String email;
    public String code;
    public String newPassword;

    public ResetPasswordRequest(String email, String code, String newPassword) {
        this.email = email;
        this.code = code;
        this.newPassword = newPassword;
    }
}
