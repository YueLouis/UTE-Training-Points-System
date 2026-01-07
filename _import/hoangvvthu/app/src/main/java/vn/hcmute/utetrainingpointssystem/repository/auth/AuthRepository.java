package vn.hcmute.utetrainingpointssystem.repository.auth;

import retrofit2.Call;
import vn.hcmute.utetrainingpointssystem.model.auth.AuthResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.LoginRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.VerifySecretRequest;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.AuthApi;

public class AuthRepository {
    private final AuthApi api;

    public AuthRepository() {
        api = RetrofitClient.getClient().create(AuthApi.class);
    }

    public Call<AuthResponse> login(LoginRequest body) {
        return api.login(body);
    }

    public Call<ForgotPasswordResponse> forgotPasswordRequest(ForgotPasswordRequest body) {
        return api.request(body);
    }

    public Call<AuthResponse> verifySecret(VerifySecretRequest body) {
        return api.verify(body);
    }
}
