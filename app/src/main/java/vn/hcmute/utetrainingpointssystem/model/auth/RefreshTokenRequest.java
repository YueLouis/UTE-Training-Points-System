package vn.hcmute.utetrainingpointssystem.model.auth;

/**
 * RefreshTokenRequest - Use refresh token to get new access token
 * Backend endpoint: POST /api/auth/refresh
 */
public class RefreshTokenRequest {
    public String refreshToken;

    public RefreshTokenRequest() {
    }

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

