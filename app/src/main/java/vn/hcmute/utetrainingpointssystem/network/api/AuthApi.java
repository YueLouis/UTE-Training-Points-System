package vn.hcmute.utetrainingpointssystem.network.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import vn.hcmute.utetrainingpointssystem.model.auth.AuthResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.LoginRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.ResetPasswordRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.RefreshTokenRequest;

/**
 * Auth API - Match backend endpoints
 * Backend: Spring Boot 3.5.8 with JWT
 */
public interface AuthApi {

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest body);

    @POST("api/auth/refresh")
    Call<AuthResponse> refresh(@Body RefreshTokenRequest body);

    @POST("api/auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest body);

    @POST("api/auth/reset-password")
    Call<ForgotPasswordResponse> resetPassword(@Body ResetPasswordRequest body);
}
