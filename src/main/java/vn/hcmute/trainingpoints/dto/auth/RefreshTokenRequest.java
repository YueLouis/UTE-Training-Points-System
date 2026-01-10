package vn.hcmute.trainingpoints.dto.auth;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RefreshTokenRequest {
    private String refreshToken;
}

