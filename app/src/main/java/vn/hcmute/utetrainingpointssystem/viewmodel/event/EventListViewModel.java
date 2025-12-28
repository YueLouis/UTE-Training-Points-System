package vn.hcmute.utetrainingpointssystem.viewmodel.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.repository.event.EventRepository;

public class EventListViewModel extends ViewModel {

    private final EventRepository repo = new EventRepository();

    private final MutableLiveData<List<EventDTO>> eventsLive = new MutableLiveData<>();
    private final MutableLiveData<String> errorLive = new MutableLiveData<>();

    public LiveData<List<EventDTO>> getEventsLive() { return eventsLive; }
    public LiveData<String> getErrorLive() { return errorLive; }

    public void fetchEvents() {
        // studentId optional -> test nhanh: null
        repo.getAllEvents(null).enqueue(new Callback<List<EventDTO>>() {
            @Override
            public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventsLive.postValue(response.body());
                } else {
                    errorLive.postValue("API lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<EventDTO>> call, Throwable t) {
                errorLive.postValue("Network lỗi: " + t.getMessage());
            }
        });
    }
}
