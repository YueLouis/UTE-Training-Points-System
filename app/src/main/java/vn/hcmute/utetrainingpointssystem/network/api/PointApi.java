package vn.hcmute.utetrainingpointssystem.network.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.hcmute.utetrainingpointssystem.model.point.StudentSummaryDTO;

public interface PointApi {
    @GET("api/points/summary/{studentId}")
    Call<StudentSummaryDTO> getSummary(
        @Path("studentId") long studentId,
        @Query("semesterId") Long semesterId
    );
}
