package vn.hcmute.utetrainingpointssystem.viewmodel.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.model.auth.AuthResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.LoginRequest;
import vn.hcmute.utetrainingpointssystem.repository.auth.AuthRepository;

public class AuthViewModel extends ViewModel {
    private final AuthRepository repo = new AuthRepository();

    private final MutableLiveData<ResultState<AuthResponse>> loginResult = new MutableLiveData<>();
    public LiveData<ResultState<AuthResponse>> getLoginResult() { return loginResult; }

    public void login(String username, String password) {
        loginResult.setValue(ResultState.loading());
        repo.login(new LoginRequest(username, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginResult.setValue(ResultState.success(response.body()));
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
}
