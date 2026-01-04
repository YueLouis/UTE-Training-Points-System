package vn.hcmute.trainingpoints.dto.auth;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ForgotPasswordResponse {
    private String email;
    private Long resetId;
    private Long expiresInSec;

    // DEMO ONLY
    private String demoCode;
}
