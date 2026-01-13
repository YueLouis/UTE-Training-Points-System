package vn.hcmute.utetrainingpointssystem.repository.auth;

import retrofit2.Call;
import vn.hcmute.utetrainingpointssystem.model.auth.AuthResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.LoginRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.RefreshTokenRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.ResetPasswordRequest;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.AuthApi;

/**
 * AuthRepository - Handle authentication API calls
 * Updated to match backend endpoints
 */
public class AuthRepository {
    private final AuthApi api;

    public AuthRepository() {
        api = RetrofitClient.getClient().create(AuthApi.class);
    }

    public Call<AuthResponse> login(LoginRequest body) {
        return api.login(body);
    }

    public Call<AuthResponse> refresh(RefreshTokenRequest body) {
        return api.refresh(body);
    }

    public Call<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest body) {
        return api.forgotPassword(body);
    }

    public Call<ForgotPasswordResponse> resetPassword(ResetPasswordRequest body) {
        return api.resetPassword(body);
    }
}
