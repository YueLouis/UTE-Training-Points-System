package vn.hcmute.trainingpoints.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class VerifySecretRequest {
    private String email;
    private String code;
    private String otp; // alias for code
}

