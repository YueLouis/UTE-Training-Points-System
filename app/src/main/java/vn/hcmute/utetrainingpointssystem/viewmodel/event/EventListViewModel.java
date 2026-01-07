package vn.hcmute.utetrainingpointssystem.viewmodel.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.repository.event.EventRepository;

public class EventListViewModel extends ViewModel {

    private final EventRepository repo = new EventRepository();

    private final MutableLiveData<ResultState<List<EventDTO>>> eventsResult = new MutableLiveData<>();
    public LiveData<ResultState<List<EventDTO>>> getEventsResult() { return eventsResult; }

    public void fetchEvents(Long studentId) {
        eventsResult.setValue(ResultState.loading());
        repo.getAllEvents(studentId).enqueue(new Callback<List<EventDTO>>() {
            @Override
            public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventsResult.setValue(ResultState.success(response.body()));
                } else {
                    eventsResult.setValue(ResultState.error("API lỗi: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<EventDTO>> call, Throwable t) {
                eventsResult.setValue(ResultState.error("Lỗi kết nối: " + t.getMessage()));
            }
        });
    }
}
