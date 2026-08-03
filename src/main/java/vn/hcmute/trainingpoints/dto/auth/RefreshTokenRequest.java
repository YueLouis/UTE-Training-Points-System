package vn.hcmute.trainingpoints.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token is required")
    @Size(max = 2048, message = "Refresh token is too long")
    private String refreshToken;
}

