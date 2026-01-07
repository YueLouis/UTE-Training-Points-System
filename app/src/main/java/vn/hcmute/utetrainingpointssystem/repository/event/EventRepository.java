package vn.hcmute.utetrainingpointssystem.repository.event;

import java.util.List;
import retrofit2.Call;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.model.event.EventRequest;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.EventApi;

public class EventRepository {
    private final EventApi api;

    public EventRepository() {
        api = RetrofitClient.getClient().create(EventApi.class);
    }

    public Call<List<EventDTO>> getAllEvents(Long studentId) {
        return api.getAllEvents(studentId);
    }

    public Call<EventDTO> getEventById(long id, Long studentId) {
        return api.getEventById(id, studentId);
    }

    public Call<List<EventDTO>> getByCategory(Long categoryId, Long studentId) {
        return api.getByCategory(categoryId, studentId);
    }

    // Manager
    public Call<EventDTO> create(EventRequest body) { return api.createEvent(body); }
    public Call<EventDTO> update(long id, EventRequest body) { return api.updateEvent(id, body); }
    public Call<Void> close(long id) { return api.closeEvent(id); }
    public Call<Void> delete(long id) { return api.deleteEvent(id); }
}
