package vn.hcmute.utetrainingpointssystem.repository.point;

import retrofit2.Call;
import vn.hcmute.utetrainingpointssystem.model.point.StudentSummaryDTO;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.PointApi;

public class PointRepository {
    private final PointApi api;

    public PointRepository() {
        api = RetrofitClient.getClient().create(PointApi.class);
    }

    public Call<StudentSummaryDTO> getSummary(Long studentId, Long semesterId) {
        return api.getSummary(studentId, semesterId);
    }
}
