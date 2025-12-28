package vn.hcmute.utetrainingpointssystem.repository.event;

import java.util.List;
import retrofit2.Call;
import vn.hcmute.utetrainingpointssystem.model.category.EventCategoryDTO;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.EventCategoryApi;

public class EventCategoryRepository {
    private final EventCategoryApi api;

    public EventCategoryRepository() {
        api = RetrofitClient.getClient().create(EventCategoryApi.class);
    }

    public Call<List<EventCategoryDTO>> getAll() {
        return api.getAll();
    }

    public Call<EventCategoryDTO> getById(long id) {
        return api.getById(id);
    }
}
