package vn.hcmute.utetrainingpointssystem.viewmodel.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.model.user.UserDTO;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.UserApi;

public class UserListViewModel extends ViewModel {

    private final MutableLiveData<ResultState<List<UserDTO>>> usersState = new MutableLiveData<>();

    public LiveData<ResultState<List<UserDTO>>> getUsersResult() {
        return usersState;
    }

    public void fetchAllUsers() {
        usersState.setValue(new ResultState.Loading<>());

        UserApi api = RetrofitClient.getClient().create(UserApi.class);
        api.getUsers().enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usersState.setValue(new ResultState.Success<>(response.body()));
                } else {
                    usersState.setValue(new ResultState.Error<>("Failed to load users"));
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                usersState.setValue(new ResultState.Error<>(t.getMessage()));
            }
        });
    }
}

