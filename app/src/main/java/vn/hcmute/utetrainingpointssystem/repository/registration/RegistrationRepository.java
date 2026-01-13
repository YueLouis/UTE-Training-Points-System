package vn.hcmute.utetrainingpointssystem.repository.registration;

import java.util.List;
import retrofit2.Call;
import vn.hcmute.utetrainingpointssystem.model.registration.EventRegistrationDTO;
import vn.hcmute.utetrainingpointssystem.model.registration.EventRegistrationRequest;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.RegistrationApi;

public class RegistrationRepository {
    private final RegistrationApi api;

    public RegistrationRepository() {
        api = RetrofitClient.getClient().create(RegistrationApi.class);
    }

    public Call<EventRegistrationDTO> register(EventRegistrationRequest body) {
        return api.register(body);
    }

    public Call<List<EventRegistrationDTO>> getByStudent(Long studentId) {
        return api.getByStudent(studentId);
    }

    public Call<List<EventRegistrationDTO>> getByEvent(Long eventId) {
        return api.getByEvent(eventId);
    }

    public Call<EventRegistrationDTO> cancel(Long id, Long userId) { 
        return api.cancel(id);
    }
    
    public Call<EventRegistrationDTO> checkin(Long eventId, Long studentId, Long adminId) { 
        return api.checkin(eventId, studentId, adminId); 
    }
    
    public Call<EventRegistrationDTO> checkout(Long eventId, Long studentId, Long adminId) { 
        return api.checkout(eventId, studentId, adminId); 
    }
    
    public Call<EventRegistrationDTO> completeSurvey(Long eventId, Long studentId, String secretCode) { 
        return api.completeSurvey(eventId, studentId);
    }
}
