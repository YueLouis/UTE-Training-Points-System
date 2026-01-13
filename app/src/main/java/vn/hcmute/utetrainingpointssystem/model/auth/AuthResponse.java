package vn.hcmute.utetrainingpointssystem.model.auth;

import com.google.gson.annotations.SerializedName;
import vn.hcmute.utetrainingpointssystem.model.user.UserDTO;

/**
 * AuthResponse - JWT Token response from backend
 * Contains: accessToken (15m), refreshToken (7d), user info
 */
public class AuthResponse {
    @SerializedName("accessToken")
    public String accessToken;

    @SerializedName("refreshToken")
    public String refreshToken;

    // For backward compatibility
    @SerializedName("token")
    public String token;

    public UserDTO user;

    public String getAccessToken() {
        return accessToken != null ? accessToken : token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
