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
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.EventApi;

public class EventListViewModel extends ViewModel {

    private final MutableLiveData<ResultState<List<EventDTO>>> eventsState = new MutableLiveData<>();

    public LiveData<ResultState<List<EventDTO>>> getEventsResult() {
        return eventsState;
    }

    public void fetchAllEvents() {
        eventsState.setValue(new ResultState.Loading<>());

        EventApi api = RetrofitClient.getClient().create(EventApi.class);
        api.getAllEvents(null).enqueue(new Callback<List<EventDTO>>() {
            @Override
            public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventsState.setValue(new ResultState.Success<>(response.body()));
                } else {
                    eventsState.setValue(new ResultState.Error<>("Failed to load events"));
                }
            }

            @Override
            public void onFailure(Call<List<EventDTO>> call, Throwable t) {
                eventsState.setValue(new ResultState.Error<>(t.getMessage()));
            }
        });
    }

    public void fetchEvents(Long categoryId) {
        eventsState.setValue(new ResultState.Loading<>());

        EventApi api = RetrofitClient.getClient().create(EventApi.class);
        api.getAllEvents(categoryId).enqueue(new Callback<List<EventDTO>>() {
            @Override
            public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventsState.setValue(new ResultState.Success<>(response.body()));
                } else {
                    eventsState.setValue(new ResultState.Error<>("Failed to load events"));
                }
            }

            @Override
            public void onFailure(Call<List<EventDTO>> call, Throwable t) {
                eventsState.setValue(new ResultState.Error<>(t.getMessage()));
            }
        });
    }

    public void deleteEvent(Long eventId, Callback<Void> callback) {
        EventApi api = RetrofitClient.getClient().create(EventApi.class);
        api.deleteEvent(eventId).enqueue(callback);
    }
}

