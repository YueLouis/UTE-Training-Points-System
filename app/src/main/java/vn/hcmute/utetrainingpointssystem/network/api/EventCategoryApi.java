package vn.hcmute.utetrainingpointssystem.network.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import vn.hcmute.utetrainingpointssystem.model.category.EventCategoryDTO;

public interface EventCategoryApi {
    @GET("api/event-categories")
    Call<List<EventCategoryDTO>> getAll();

    @GET("api/event-categories/{id}")
    Call<EventCategoryDTO> getById(@Path("id") Long id);
}
