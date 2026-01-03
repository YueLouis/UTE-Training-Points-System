package vn.hcmute.utetrainingpointssystem.viewmodel.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.model.category.EventCategoryDTO;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.model.event.EventRequest;
import vn.hcmute.utetrainingpointssystem.model.registration.EventRegistrationDTO;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.EventApi;
import vn.hcmute.utetrainingpointssystem.network.api.EventCategoryApi;
import vn.hcmute.utetrainingpointssystem.network.api.RegistrationApi;


public class EventManageViewModel extends ViewModel {

    private final EventApi eventApi =
            RetrofitClient.getClient().create(EventApi.class);

    private final EventCategoryApi categoryApi =
            RetrofitClient.getClient().create(EventCategoryApi.class);

    private final RegistrationApi registrationApi =
            RetrofitClient.getClient().create(RegistrationApi.class);


    // ====== STATES ======
    private final MutableLiveData<ResultState<List<EventDTO>>> eventsState = new MutableLiveData<>();
    public LiveData<ResultState<List<EventDTO>>> getEventsState() { return eventsState; }

    private final MutableLiveData<ResultState<List<EventCategoryDTO>>> categoriesState = new MutableLiveData<>();
    public LiveData<ResultState<List<EventCategoryDTO>>> getCategoriesState() { return categoriesState; }

    private final MutableLiveData<ResultState<EventDTO>> eventDetailState = new MutableLiveData<>();
    public LiveData<ResultState<EventDTO>> getEventDetailState() { return eventDetailState; }

    private final MutableLiveData<ResultState<Void>> actionState = new MutableLiveData<>();
    public LiveData<ResultState<Void>> getActionState() { return actionState; }

    // ====== COUNT REGISTERED STATE ======
    private final MutableLiveData<ResultState<Integer>> registrationCountState = new MutableLiveData<>();
    public LiveData<ResultState<Integer>> getRegistrationCountState() { return registrationCountState; }


    // ===== FILTER =====
    public void loadEventsByCategory(Long categoryId) {
        eventsState.setValue(new ResultState.Loading<>());

        if (categoryId == null) {
            loadAllEvents();
            return;
        }

        eventApi.getByCategory(categoryId, null).enqueue(new Callback<List<EventDTO>>() {
            @Override
            public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> response) {
                if (response.isSuccessful()) {
                    eventsState.setValue(new ResultState.Success<>(response.body()));
                } else {
                    eventsState.setValue(new ResultState.Error<>("Load by category failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<EventDTO>> call, Throwable t) {
                eventsState.setValue(new ResultState.Error<>(t.getMessage()));
            }
        });
    }

    // ====== LOAD EVENTS LIST  ======
    public void loadAllEvents() {
        eventsState.setValue(new ResultState.Loading<>());

        // studentId query: admin thường truyền null để lấy all (nếu BE cho)
        eventApi.getAllEvents(null).enqueue(new Callback<List<EventDTO>>() {
            @Override
            public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> response) {
                if (response.isSuccessful()) {
                    eventsState.setValue(new ResultState.Success<>(response.body()));
                } else {
                    eventsState.setValue(new ResultState.Error<>("Load events failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<EventDTO>> call, Throwable t) {
                eventsState.setValue(new ResultState.Error<>(t.getMessage()));
            }
        });
    }

    // ====== LOAD CATEGORIES (for spinner) ======
    public void loadCategories() {
        categoriesState.setValue(new ResultState.Loading<>());

        categoryApi.getAll().enqueue(new Callback<List<EventCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<EventCategoryDTO>> call, Response<List<EventCategoryDTO>> response) {
                if (response.isSuccessful()) {
                    categoriesState.setValue(new ResultState.Success<>(response.body()));
                } else {
                    categoriesState.setValue(new ResultState.Error<>("Load categories failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<EventCategoryDTO>> call, Throwable t) {
                categoriesState.setValue(new ResultState.Error<>(t.getMessage()));
            }
        });
    }

    // ====== LOAD EVENT DETAIL (for detail/edit) ======
    public void loadEventById(Long id) {
        eventDetailState.setValue(new ResultState.Loading<>());

        eventApi.getEventById(id).enqueue(new Callback<EventDTO>() {
            @Override
            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                if (response.isSuccessful()) {
                    eventDetailState.setValue(new ResultState.Success<>(response.body()));
                } else {
                    eventDetailState.setValue(new ResultState.Error<>("Load event failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<EventDTO> call, Throwable t) {
                eventDetailState.setValue(new ResultState.Error<>(t.getMessage()));
            }
        });
    }


    // ====== COUNT REGISTERED ======
    public void loadRegistrationCount(Long eventId) {
        registrationCountState.setValue(new ResultState.Loading<>());

        registrationApi.getByEvent(eventId).enqueue(new Callback<List<EventRegistrationDTO>>() {
            @Override
            public void onResponse(Call<List<EventRegistrationDTO>> call, Response<List<EventRegistrationDTO>> response) {
                if (response.isSuccessful()) {
                    List<EventRegistrationDTO> list = response.body();
                    int count = (list == null) ? 0 : list.size();
                    registrationCountState.setValue(new ResultState.Success<>(count));
                } else {
                    registrationCountState.setValue(new ResultState.Error<>("Load registrations failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<EventRegistrationDTO>> call, Throwable t) {
                registrationCountState.setValue(new ResultState.Error<>(t.getMessage()));
            }
        });
    }
    // ====== CREATE ======
    public void createEvent(EventRequest body) {
        actionState.setValue(new ResultState.Loading<>());

        eventApi.createEvent(body).enqueue(new Callback<EventDTO>() {
            @Override
            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                if (response.isSuccessful()) {
                    actionState.setValue(new ResultState.Success<>(null));
                } else {
                    actionState.setValue(new ResultState.Error<>("Create failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<EventDTO> call, Throwable t) {
                actionState.setValue(new ResultState.Error<>(t.getMessage()));
            }
        });
    }

    // ====== UPDATE ======
    public void updateEvent(Long id, EventRequest body) {
        actionState.setValue(new ResultState.Loading<>());

        eventApi.updateEvent(id, body).enqueue(new Callback<EventDTO>() {
            @Override
            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                if (response.isSuccessful()) {
                    actionState.setValue(new ResultState.Success<>(null));
                } else {
                    actionState.setValue(new ResultState.Error<>("Update failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<EventDTO> call, Throwable t) {
                actionState.setValue(new ResultState.Error<>(t.getMessage()));
            }
        });
    }

    // ====== CLOSE ======
    public void closeEvent(Long id) {
        actionState.setValue(new ResultState.Loading<>());

        eventApi.closeEvent(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    actionState.setValue(new ResultState.Success<>(null));
                } else {
                    actionState.setValue(new ResultState.Error<>("Close failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                actionState.setValue(new ResultState.Error<>(t.getMessage()));
            }
        });
    }

    // ====== DELETE ======
    public void deleteEvent(Long id) {
        actionState.setValue(new ResultState.Loading<>());

        eventApi.deleteEvent(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    actionState.setValue(new ResultState.Success<>(null));
                } else {
                    actionState.setValue(new ResultState.Error<>("Delete failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                actionState.setValue(new ResultState.Error<>(t.getMessage()));
            }
        });
    }
}
