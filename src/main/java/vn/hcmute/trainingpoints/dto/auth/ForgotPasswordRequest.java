package vn.hcmute.trainingpoints.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ForgotPasswordRequest {
    private String email;
}
