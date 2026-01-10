package vn.hcmute.trainingpoints.dto.auth;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthResponse {
    private String token; // JWT access token
    private String refreshToken; // JWT refresh token
    private AuthUser user;
}
