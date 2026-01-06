package vn.hcmute.utetrainingpointssystem.network.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import vn.hcmute.utetrainingpointssystem.model.notification.NotificationDTO;

public interface NotificationApi {
    @GET("api/notifications/user/{userId}")
    Call<List<NotificationDTO>> getNotifications(@Path("userId") Long userId);

    @PUT("api/notifications/{id}/read")
    Call<Void> markAsRead(@Path("id") Long id);
}
