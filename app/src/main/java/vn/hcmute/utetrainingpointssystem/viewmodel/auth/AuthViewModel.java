package vn.hcmute.utetrainingpointssystem.viewmodel.auth;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;
import vn.hcmute.utetrainingpointssystem.model.auth.AuthResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.ForgotPasswordResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.LoginRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.RefreshTokenRequest;
import vn.hcmute.utetrainingpointssystem.model.auth.ResetPasswordRequest;
import vn.hcmute.utetrainingpointssystem.repository.auth.AuthRepository;

/**
 * AuthViewModel - Handle authentication business logic
 * Manages JWT tokens (access + refresh)
 */
public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository repo = new AuthRepository();
    private final TokenManager tokenManager;

    private final MutableLiveData<ResultState<AuthResponse>> loginResult = new MutableLiveData<>();
    private final MutableLiveData<ResultState<ForgotPasswordResponse>> forgotPasswordResult = new MutableLiveData<>();
    private final MutableLiveData<ResultState<ForgotPasswordResponse>> resetPasswordResult = new MutableLiveData<>();

    public AuthViewModel(Application application) {
        super(application);
        tokenManager = new TokenManager(application);
    }

    public LiveData<ResultState<AuthResponse>> getLoginResult() {
        return loginResult;
    }

    public LiveData<ResultState<ForgotPasswordResponse>> getForgotPasswordResult() {
        return forgotPasswordResult;
    }

    public LiveData<ResultState<ForgotPasswordResponse>> getResetPasswordResult() {
        return resetPasswordResult;
    }

    public void login(String username, String password) {
        loginResult.setValue(ResultState.loading());
        repo.login(new LoginRequest(username, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();

                    // Save tokens to TokenManager
                    String accessToken = auth.getAccessToken();
                    String refreshToken = auth.getRefreshToken();

                    if (accessToken != null && auth.user != null) {
                        tokenManager.saveAuth(
                            accessToken,
                            refreshToken != null ? refreshToken : "",
                            auth.user.id,
                            auth.user.role != null ? auth.user.role : "STUDENT"
                        );
                    }

                    loginResult.setValue(ResultState.success(auth));
                } else {
                    loginResult.setValue(ResultState.error("Đăng nhập thất bại: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                loginResult.setValue(ResultState.error("Lỗi mạng: " + t.getMessage()));
            }
        });
    }

    public void forgotPassword(String email) {
        forgotPasswordResult.setValue(ResultState.loading());
        repo.forgotPassword(new ForgotPasswordRequest(email)).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    forgotPasswordResult.setValue(ResultState.success(response.body()));
                } else {
                    forgotPasswordResult.setValue(ResultState.error("Gửi email thất bại: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                forgotPasswordResult.setValue(ResultState.error("Lỗi mạng: " + t.getMessage()));
            }
        });
    }

    public void resetPassword(String token, String newPassword) {
        resetPasswordResult.setValue(ResultState.loading());
        repo.resetPassword(new ResetPasswordRequest(token, newPassword)).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resetPasswordResult.setValue(ResultState.success(response.body()));
                } else {
                    resetPasswordResult.setValue(ResultState.error("Đặt lại mật khẩu thất bại: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                resetPasswordResult.setValue(ResultState.error("Lỗi mạng: " + t.getMessage()));
            }
        });
    }

    public void logout() {
        tokenManager.clear();
    }
}
