package vn.hcmute.utetrainingpointssystem.network.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import vn.hcmute.utetrainingpointssystem.model.registration.EventRegistrationDTO;
import vn.hcmute.utetrainingpointssystem.model.registration.EventRegistrationRequest;

public interface RegistrationApi {
    @POST("api/event-registrations")
    Call<EventRegistrationDTO> register(@Body EventRegistrationRequest body);

    @GET("api/event-registrations/by-student/{studentId}")
    Call<List<EventRegistrationDTO>> getByStudent(@Path("studentId") Long studentId);

    @GET("api/event-registrations/by-event/{eventId}")
    Call<List<EventRegistrationDTO>> getByEvent(@Path("eventId") Long eventId);

    @PUT("api/event-registrations/{id}/cancel")
    Call<EventRegistrationDTO> cancel(@Path("id") Long id);

    @PUT("api/event-registrations/{eventId}/check-in/{studentId}")
    Call<EventRegistrationDTO> checkin(@Path("eventId") Long eventId, @Path("studentId") Long studentId);

    @PUT("api/event-registrations/{eventId}/check-out/{studentId}")
    Call<EventRegistrationDTO> checkout(@Path("eventId") Long eventId, @Path("studentId") Long studentId);

    @PUT("api/event-registrations/{eventId}/complete-survey/{studentId}")
    Call<EventRegistrationDTO> completeSurvey(@Path("eventId") Long eventId, @Path("studentId") Long studentId);
}
