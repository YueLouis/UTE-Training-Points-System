package vn.hcmute.utetrainingpointssystem.network.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.model.event.EventRequest;

public interface EventApi {
    @GET("api/events")
    Call<List<EventDTO>> getAllEvents(@Query("studentId") Long studentId);

    @GET("api/events/{id}")
    Call<EventDTO> getEventById(
            @Path("id") Long id,
            @Query("studentId") Long studentId
    );

    @GET("api/events/by-category/{categoryId}")
    Call<List<EventDTO>> getByCategory(
            @Path("categoryId") Long categoryId,
            @Query("studentId") Long studentId
    );

    // Manager
    @POST("api/events")
    Call<EventDTO> createEvent(@Body EventRequest body);

    @PUT("api/events/{id}")
    Call<EventDTO> updateEvent(@Path("id") Long id, @Body EventRequest body);

    @POST("api/events/{id}/close")
    Call<Void> closeEvent(@Path("id") Long id);

    @DELETE("api/events/{id}")
    Call<Void> deleteEvent(@Path("id") Long id);
}
