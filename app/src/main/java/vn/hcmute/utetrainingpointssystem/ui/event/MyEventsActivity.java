package vn.hcmute.utetrainingpointssystem.ui.event;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.model.registration.EventRegistrationDTO;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.EventApi;
import vn.hcmute.utetrainingpointssystem.network.api.RegistrationApi;

public class MyEventsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private EventAdapter adapter;
    private TokenManager tokenManager;
    private Map<Long, Long> eventToRegMap = new HashMap<>();
    private List<EventDTO> displayEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        tokenManager = new TokenManager(this);
        rv = findViewById(R.id.rvMyEvents);
        rv.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new EventAdapter();
        adapter.setShowCancelButton(true);
        rv.setAdapter(adapter);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        adapter.setOnActionClickListener(event -> {
            Long regId = eventToRegMap.get(event.id);
            if (regId != null) {
                cancelRegistration(regId);
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin đăng ký", Toast.LENGTH_SHORT).show();
            }
        });

        loadMyEvents();
    }

    private void loadMyEvents() {
        Long studentId = tokenManager.getStudentId();
        if (studentId == null) return;

        RegistrationApi regApi = RetrofitClient.getClient().create(RegistrationApi.class);
        EventApi eventApi = RetrofitClient.getClient().create(EventApi.class);

        regApi.getByStudent(studentId).enqueue(new Callback<List<EventRegistrationDTO>>() {
            @Override
            public void onResponse(Call<List<EventRegistrationDTO>> call, Response<List<EventRegistrationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EventRegistrationDTO> regs = response.body();
                    displayEvents.clear();
                    eventToRegMap.clear();

                    if (regs.isEmpty()) {
                        adapter.submitList(new ArrayList<>());
                        Toast.makeText(MyEventsActivity.this, "Bạn chưa đăng ký sự kiện nào", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (EventRegistrationDTO reg : regs) {
                        if ("CANCELLED".equals(reg.status)) continue;
                        
                        eventToRegMap.put(reg.eventId, reg.id);
                        eventApi.getEventById(reg.eventId).enqueue(new Callback<EventDTO>() {
                            @Override
                            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    displayEvents.add(response.body());
                                    adapter.submitList(new ArrayList<>(displayEvents));
                                }
                            }
                            @Override public void onFailure(Call<EventDTO> call, Throwable t) {}
                        });
                    }
                }
            }
            @Override public void onFailure(Call<List<EventRegistrationDTO>> call, Throwable t) {
                Toast.makeText(MyEventsActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelRegistration(long regId) {
        Long userId = tokenManager.getUserId();
        if (userId == null) return;

        RegistrationApi api = RetrofitClient.getClient().create(RegistrationApi.class);
        api.cancel(regId).enqueue(new Callback<EventRegistrationDTO>() {
            @Override
            public void onResponse(Call<EventRegistrationDTO> call, Response<EventRegistrationDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MyEventsActivity.this, "Đã hủy đăng ký thành công", Toast.LENGTH_SHORT).show();
                    loadMyEvents(); 
                } else {
                    Log.e("CANCEL_ERROR", "Code: " + response.code() + " Message: " + response.message());
                    Toast.makeText(MyEventsActivity.this, "Hủy thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<EventRegistrationDTO> call, Throwable t) {
                Toast.makeText(MyEventsActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
