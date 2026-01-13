package vn.hcmute.utetrainingpointssystem.network.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import vn.hcmute.utetrainingpointssystem.model.semester.SemesterDTO;

public interface SemesterApi {
    @GET("api/semesters")
    Call<List<SemesterDTO>> getAll();

    @GET("api/semesters/{id}")
    Call<SemesterDTO> getById(@Path("id") Long id);
}

