package vn.hcmute.trainingpoints.dto.auth;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthResponse {
    private String token; // token giả
    private AuthUser user;
}
