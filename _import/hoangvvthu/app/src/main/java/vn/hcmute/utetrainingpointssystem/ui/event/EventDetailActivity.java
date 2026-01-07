package vn.hcmute.utetrainingpointssystem.ui.event;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.EventApi;
import vn.hcmute.utetrainingpointssystem.viewmodel.registration.EventRegistrationViewModel;

public class EventDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvInfo, tvDescription;
    private Button btnRegister, btnCancel, btnCompleteSurvey, btnOpenSurvey;
    private ImageView btnBack;
    private View cardDetail;
    private EventRegistrationViewModel registrationViewModel;
    private TokenManager tokenManager;
    private long eventId;
    private EventDTO currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventId = getIntent().getLongExtra("EVENT_ID", -1);
        tokenManager = new TokenManager(this);
        registrationViewModel = new ViewModelProvider(this).get(EventRegistrationViewModel.class);

        initViews();
        setupObservers();
        loadEventDetails();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvInfo = findViewById(R.id.tvDetailInfo);
        tvDescription = findViewById(R.id.tvDetailDescription);
        btnRegister = findViewById(R.id.btnRegister);
        btnCancel = findViewById(R.id.btnCancel);
        btnCompleteSurvey = findViewById(R.id.btnCompleteSurvey);
        btnBack = findViewById(R.id.btnBack);
        cardDetail = findViewById(R.id.cardDetail);

        // Nút mở link khảo sát cho sự kiện Online
        btnOpenSurvey = new Button(this);
        btnOpenSurvey.setText("Mở Link Khảo Sát");
        btnOpenSurvey.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF9800")));
        btnOpenSurvey.setTextColor(android.graphics.Color.WHITE);
        btnOpenSurvey.setVisibility(View.GONE);
        ((android.widget.LinearLayout) btnRegister.getParent()).addView(btnOpenSurvey, 1);

        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            if (currentEvent == null) return;
            if ("CLOSED".equals(currentEvent.getStatus())) {
                Toast.makeText(this, "Sự kiện này đã đóng đăng ký!", Toast.LENGTH_LONG).show();
                return;
            }
            if (currentEvent.getRemainingSlots() != null && currentEvent.getRemainingSlots() <= 0) {
                Toast.makeText(this, "Sự kiện đã hết chỗ!", Toast.LENGTH_LONG).show();
                return;
            }
            Long studentId = tokenManager.getStudentId();
            if (studentId != null) {
                registrationViewModel.registerEvent(studentId, eventId);
            }
        });

        btnCancel.setOnClickListener(v -> {
            Long studentId = tokenManager.getStudentId();
            if (studentId != null) {
                registrationViewModel.cancelRegistrationByEvent(eventId, studentId);
            }
        });

        btnOpenSurvey.setOnClickListener(v -> {
            if (currentEvent != null && currentEvent.getSurveyUrl() != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentEvent.getSurveyUrl()));
                startActivity(intent);
            }
        });

        btnCompleteSurvey.setOnClickListener(v -> showSecretCodeDialog());
    }

    private void showSecretCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập Mã Bí Mật");
        builder.setMessage("Nhập mã bí mật từ ban tổ chức để hoàn thành khảo sát:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String codeEntered = input.getText().toString().trim();
            if (currentEvent != null && codeEntered.equals(currentEvent.getSurveySecretCode())) {
                registrationViewModel.completeSurvey(eventId, tokenManager.getStudentId());
            } else {
                Toast.makeText(this, "Mã bí mật không chính xác!", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void setupObservers() {
        registrationViewModel.getRegistrationResult().observe(this, registration -> {
            Toast.makeText(this, "Thao tác thành công!", Toast.LENGTH_SHORT).show();
            loadEventDetails(); 
        });

        registrationViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadEventDetails() {
        EventApi api = RetrofitClient.getClient().create(EventApi.class);
        api.getEventById(eventId, tokenManager.getStudentId()).enqueue(new Callback<EventDTO>() {
            @Override
            public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentEvent = response.body();
                    updateUI(currentEvent);
                }
            }
            @Override
            public void onFailure(Call<EventDTO> call, Throwable t) {
                Toast.makeText(EventDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(EventDTO event) {
        tvTitle.setText(event.getTitle());
        String info = String.format("%s | %s | %s", 
                event.getStartTime(), 
                "ONLINE".equals(event.getEventMode()) ? "Online" : event.getLocation(),
                "CLOSED".equals(event.getStatus()) ? "Đã đóng" : "Còn lại: " + event.getRemainingSlots());
        tvInfo.setText(info);
        tvDescription.setText(event.getDescription());

        // Update colors
        int color;
        if (event.getCategoryId() != null && event.getCategoryId() == 2) color = android.graphics.Color.parseColor("#42A5F5");
        else if (event.getCategoryId() != null && event.getCategoryId() == 3) color = android.graphics.Color.parseColor("#66BB6A");
        else color = android.graphics.Color.parseColor("#26C6DA");
        cardDetail.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));

        // Nút Đăng ký và Hủy
        btnRegister.setVisibility(!event.registered ? View.VISIBLE : View.GONE);
        btnCancel.setVisibility(event.registered ? View.VISIBLE : View.GONE);
        
        // Vô hiệu hóa nút Đăng ký nếu hết slot hoặc đã đóng
        if (("CLOSED".equals(event.getStatus()) || (event.getRemainingSlots() != null && event.getRemainingSlots() <= 0)) && !event.registered) {
            btnRegister.setEnabled(false);
            btnRegister.setAlpha(0.5f);
            btnRegister.setText("Không thể đăng ký");
        } else {
            btnRegister.setEnabled(true);
            btnRegister.setAlpha(1.0f);
            btnRegister.setText("Đăng kí");
        }
        
        // Xử lý ONLINE flow
        if ("ONLINE".equals(event.getEventMode()) && event.registered) {
             btnOpenSurvey.setVisibility(View.VISIBLE);
             btnCompleteSurvey.setVisibility(View.VISIBLE);
             btnCompleteSurvey.setText("Nhập mã bí mật");
        } else {
             btnOpenSurvey.setVisibility(View.GONE);
             btnCompleteSurvey.setVisibility(View.GONE);
        }
    }
}
