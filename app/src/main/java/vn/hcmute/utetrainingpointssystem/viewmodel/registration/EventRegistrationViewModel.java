package vn.hcmute.utetrainingpointssystem.viewmodel.registration;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;
import vn.hcmute.utetrainingpointssystem.model.registration.EventRegistrationDTO;
import vn.hcmute.utetrainingpointssystem.model.registration.EventRegistrationRequest;
import vn.hcmute.utetrainingpointssystem.repository.registration.RegistrationRepository;

/**
 * EventRegistrationViewModel - Handle event registration logic
 */
public class EventRegistrationViewModel extends AndroidViewModel {
    private final RegistrationRepository repo = new RegistrationRepository();
    private final TokenManager tokenManager;

    private final MutableLiveData<ResultState<EventRegistrationDTO>> registerResult = new MutableLiveData<>();
    private final MutableLiveData<ResultState<List<EventRegistrationDTO>>> myRegistrationsResult = new MutableLiveData<>();
    private final MutableLiveData<ResultState<EventRegistrationDTO>> cancelResult = new MutableLiveData<>();
    private final MutableLiveData<ResultState<EventRegistrationDTO>> completeSurveyResult = new MutableLiveData<>();

    public EventRegistrationViewModel(Application application) {
        super(application);
        tokenManager = new TokenManager(application);
    }

    public LiveData<ResultState<EventRegistrationDTO>> getRegisterResult() {
        return registerResult;
    }

    public LiveData<ResultState<List<EventRegistrationDTO>>> getMyRegistrationsResult() {
        return myRegistrationsResult;
    }

    public LiveData<ResultState<EventRegistrationDTO>> getCancelResult() {
        return cancelResult;
    }

    public LiveData<ResultState<EventRegistrationDTO>> getCompleteSurveyResult() {
        return completeSurveyResult;
    }

    public void register(Long eventId, String note) {
        Long studentId = tokenManager.getStudentId();
        if (studentId == null) {
            registerResult.setValue(ResultState.error("Chưa đăng nhập"));
            return;
        }

        registerResult.setValue(ResultState.loading());
        EventRegistrationRequest request = new EventRegistrationRequest(eventId, studentId, note);

        repo.register(request).enqueue(new Callback<EventRegistrationDTO>() {
            @Override
            public void onResponse(Call<EventRegistrationDTO> call, Response<EventRegistrationDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    registerResult.setValue(ResultState.success(response.body()));
                } else {
                    registerResult.setValue(ResultState.error("Đăng ký thất bại: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<EventRegistrationDTO> call, Throwable t) {
                registerResult.setValue(ResultState.error("Lỗi mạng: " + t.getMessage()));
            }
        });
    }

    public void loadMyRegistrations() {
        Long studentId = tokenManager.getStudentId();
        if (studentId == null) {
            myRegistrationsResult.setValue(ResultState.error("Chưa đăng nhập"));
            return;
        }

        myRegistrationsResult.setValue(ResultState.loading());
        repo.getByStudent(studentId).enqueue(new Callback<List<EventRegistrationDTO>>() {
            @Override
            public void onResponse(Call<List<EventRegistrationDTO>> call, Response<List<EventRegistrationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    myRegistrationsResult.setValue(ResultState.success(response.body()));
                } else {
                    myRegistrationsResult.setValue(ResultState.error("Tải danh sách thất bại: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<EventRegistrationDTO>> call, Throwable t) {
                myRegistrationsResult.setValue(ResultState.error("Lỗi mạng: " + t.getMessage()));
            }
        });
    }

    public void cancel(Long registrationId) {
        Long userId = tokenManager.getUserId();
        if (userId == null) {
            cancelResult.setValue(ResultState.error("Chưa đăng nhập"));
            return;
        }

        cancelResult.setValue(ResultState.loading());
        repo.cancel(registrationId, userId).enqueue(new Callback<EventRegistrationDTO>() {
            @Override
            public void onResponse(Call<EventRegistrationDTO> call, Response<EventRegistrationDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cancelResult.setValue(ResultState.success(response.body()));
                } else {
                    cancelResult.setValue(ResultState.error("Hủy đăng ký thất bại: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<EventRegistrationDTO> call, Throwable t) {
                cancelResult.setValue(ResultState.error("Lỗi mạng: " + t.getMessage()));
            }
        });
    }

    public void completeSurvey(Long eventId, String secretCode) {
        Long studentId = tokenManager.getStudentId();
        if (studentId == null) {
            completeSurveyResult.setValue(ResultState.error("Chưa đăng nhập"));
            return;
        }

        completeSurveyResult.setValue(ResultState.loading());
        repo.completeSurvey(eventId, studentId, secretCode).enqueue(new Callback<EventRegistrationDTO>() {
            @Override
            public void onResponse(Call<EventRegistrationDTO> call, Response<EventRegistrationDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    completeSurveyResult.setValue(ResultState.success(response.body()));
                } else {
                    completeSurveyResult.setValue(ResultState.error("Hoàn thành khảo sát thất bại: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<EventRegistrationDTO> call, Throwable t) {
                completeSurveyResult.setValue(ResultState.error("Lỗi mạng: " + t.getMessage()));
            }
        });
    }
}
