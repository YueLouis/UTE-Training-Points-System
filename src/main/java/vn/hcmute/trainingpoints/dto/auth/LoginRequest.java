package vn.hcmute.trainingpoints.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Size(max = 320, message = "Username is too long")
    private String username; // can be email or studentCode

    @NotBlank(message = "Password is required")
    @Size(max = 72, message = "Password is too long")
    private String password;
}
