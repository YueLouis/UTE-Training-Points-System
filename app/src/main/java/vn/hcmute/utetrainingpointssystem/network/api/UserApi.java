package vn.hcmute.utetrainingpointssystem.network.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import vn.hcmute.utetrainingpointssystem.model.user.UserDTO;

public interface UserApi {
    @GET("api/users")
    Call<List<UserDTO>> getAllUsers();

    @GET("api/users/{id}")
    Call<UserDTO> getUserById(@Path("id") Long id);
}
