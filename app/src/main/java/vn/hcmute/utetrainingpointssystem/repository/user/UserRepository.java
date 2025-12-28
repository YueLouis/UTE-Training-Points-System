package vn.hcmute.utetrainingpointssystem.repository.user;

import java.util.List;
import retrofit2.Call;
import vn.hcmute.utetrainingpointssystem.model.user.UserDTO;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.UserApi;

public class UserRepository {
    private final UserApi api;

    public UserRepository() {
        api = RetrofitClient.getClient().create(UserApi.class);
    }

    public Call<List<UserDTO>> getAllUsers() {
        return api.getAllUsers();
    }

    public Call<UserDTO> getUserById(Long id) {
        return api.getUserById(id);
    }
}
