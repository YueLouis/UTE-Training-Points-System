package vn.hcmute.trainingpoints.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {
    @NotBlank(message = "Token is required")
    @Size(max = 512, message = "Token is too long")
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    private String newPassword;
}

