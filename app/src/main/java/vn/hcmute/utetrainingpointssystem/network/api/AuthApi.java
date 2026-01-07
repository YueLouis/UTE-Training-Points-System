package vn.hcmute.utetrainingpointssystem.network.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import vn.hcmute.utetrainingpointssystem.model.auth.AuthResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.LoginRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.ResetPasswordRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.VerifySecretRequest;

public interface AuthApi {

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest body);

    @POST("api/auth/forgot-password/request")
    Call<ForgotPasswordResponse> request(@Body ForgotPasswordRequest body);

    @POST("api/auth/forgot-password/verify")
    Call<AuthResponse> verify(@Body VerifySecretRequest body);

    @POST("api/auth/forgot-password/reset")
    Call<AuthResponse> resetPassword(@Body ResetPasswordRequest body);
}
