package vn.hcmute.trainingpoints.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 320, message = "Email is too long")
    private String email;
}

