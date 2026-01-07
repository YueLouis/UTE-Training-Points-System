package vn.hcmute.utetrainingpointssystem.model.auth;

public class VerifySecretRequest {
    public String email;
    public String code;

    public VerifySecretRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
