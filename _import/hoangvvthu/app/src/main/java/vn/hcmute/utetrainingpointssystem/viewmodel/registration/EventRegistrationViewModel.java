package vn.hcmute.utetrainingpointssystem.viewmodel.registration;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.model.registration.EventRegistrationDTO;
import vn.hcmute.utetrainingpointssystem.model.registration.EventRegistrationRequest;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.RegistrationApi;

public class EventRegistrationViewModel extends ViewModel {
    private final RegistrationApi registrationApi;

    private final MutableLiveData<List<EventRegistrationDTO>> registrations = new MutableLiveData<>();
    private final MutableLiveData<EventRegistrationDTO> registrationResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public EventRegistrationViewModel() {
        registrationApi = RetrofitClient.getClient().create(RegistrationApi.class);
    }

    public LiveData<List<EventRegistrationDTO>> getRegistrations() { return registrations; }
    public LiveData<EventRegistrationDTO> getRegistrationResult() { return registrationResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void registerEvent(Long studentId, Long eventId) {
        isLoading.setValue(true);
        registrationApi.register(new EventRegistrationRequest(studentId, eventId)).enqueue(new Callback<EventRegistrationDTO>() {
            @Override
            public void onResponse(Call<EventRegistrationDTO> call, Response<EventRegistrationDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    registrationResult.setValue(response.body());
                } else {
                    if (response.code() == 400) {
                        errorMessage.setValue("Đăng ký thất bại: Sự kiện đã hết chỗ hoặc bạn đã đăng ký rồi.");
                    } else {
                        errorMessage.setValue("Đăng ký thất bại: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<EventRegistrationDTO> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void cancelRegistrationByEvent(Long eventId, Long studentId) {
        isLoading.setValue(true);
        // Using common pattern: find registration first or call dedicated cancel by event if exists
        // Assuming we need to cancel the specific registration
        registrationApi.getByStudent(studentId).enqueue(new Callback<List<EventRegistrationDTO>>() {
            @Override
            public void onResponse(Call<List<EventRegistrationDTO>> call, Response<List<EventRegistrationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (EventRegistrationDTO r : response.body()) {
                        if (r.getEventId() != null && r.getEventId().equals(eventId) && !"CANCELLED".equals(r.getStatus())) {
                            performCancel(r.getId());
                            return;
                        }
                    }
                    isLoading.setValue(false);
                    errorMessage.setValue("Không tìm thấy thông tin đăng ký để hủy.");
                } else {
                    isLoading.setValue(false);
                    errorMessage.setValue("Lỗi khi tìm thông tin đăng ký.");
                }
            }

            @Override
            public void onFailure(Call<List<EventRegistrationDTO>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    private void performCancel(Long registrationId) {
        registrationApi.cancel(registrationId).enqueue(new Callback<EventRegistrationDTO>() {
            @Override
            public void onResponse(Call<EventRegistrationDTO> call, Response<EventRegistrationDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    registrationResult.setValue(response.body());
                } else {
                    errorMessage.setValue("Hủy đăng ký thất bại.");
                }
            }

            @Override
            public void onFailure(Call<EventRegistrationDTO> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public void fetchStudentRegistrations(Long studentId) {
        isLoading.setValue(true);
        registrationApi.getByStudent(studentId).enqueue(new Callback<List<EventRegistrationDTO>>() {
            @Override
            public void onResponse(Call<List<EventRegistrationDTO>> call, Response<List<EventRegistrationDTO>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    registrations.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải danh sách sự kiện");
                }
            }

            @Override
            public void onFailure(Call<List<EventRegistrationDTO>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public void completeSurvey(Long eventId, Long studentId) {
        isLoading.setValue(true);
        registrationApi.completeSurvey(eventId, studentId).enqueue(new Callback<EventRegistrationDTO>() {
            @Override
            public void onResponse(Call<EventRegistrationDTO> call, Response<EventRegistrationDTO> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    registrationResult.setValue(response.body());
                } else {
                    errorMessage.setValue("Xác nhận hoàn thành thất bại");
                }
            }

            @Override
            public void onFailure(Call<EventRegistrationDTO> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }
}
