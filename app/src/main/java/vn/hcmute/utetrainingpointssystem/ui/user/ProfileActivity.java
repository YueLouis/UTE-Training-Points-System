package vn.hcmute.utetrainingpointssystem.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;
import vn.hcmute.utetrainingpointssystem.model.point.StudentSummaryDTO;
import vn.hcmute.utetrainingpointssystem.model.user.UserDTO;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.PointApi;
import vn.hcmute.utetrainingpointssystem.network.api.UserApi;
import vn.hcmute.utetrainingpointssystem.ui.event.MyEventsActivity;

public class ProfileActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private TextView tvFullName, tvStudentCode, tvEmail, tvPhone, tvClassName;
    private TextView tvDRL, tvCTXH, tvCDDN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tokenManager = new TokenManager(this);

        // Bind Views
        tvFullName = findViewById(R.id.tvFullName);
        tvStudentCode = findViewById(R.id.tvStudentCode);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvClassName = findViewById(R.id.tvClassName);
        tvDRL = findViewById(R.id.tvDRL);
        tvCTXH = findViewById(R.id.tvCTXH);
        tvCDDN = findViewById(R.id.tvCDDN);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnMyEvents).setOnClickListener(v -> 
            startActivity(new Intent(this, MyEventsActivity.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            tokenManager.clear();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadUser();
    }

    private void loadUser() {
        Long userId = tokenManager.getUserId();
        String userRole = tokenManager.getRole();
        boolean isAdmin = "ADMIN".equals(userRole);

        if (userId == null) return;

        UserApi api = RetrofitClient.getClient().create(UserApi.class);
        api.getUserById(userId).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDTO u = response.body();
                    tvFullName.setText(u.fullName != null ? u.fullName : "--");
                    tvStudentCode.setText(u.studentCode != null ? u.studentCode : "--");
                    tvEmail.setText(u.email != null ? u.email : "--");
                    tvPhone.setText(u.phone != null ? u.phone : "--");
                    tvClassName.setText(u.className != null ? u.className : "--");
                    
                    // Only load points for students, not admin
                    if (!isAdmin) {
                        loadPoints(u.id);
                    } else {
                        // Hide points section for admin
                        findViewById(R.id.layoutPoints).setVisibility(android.view.View.GONE);
                        findViewById(R.id.tvHkBadge).setVisibility(android.view.View.GONE);
                    }
                }
            }
            @Override public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi tải thông tin user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPoints(long studentId) {
        PointApi api = RetrofitClient.getClient().create(PointApi.class);
        // Truyền thêm semesterId = 1L để lấy đúng học kỳ có điểm theo CSDL của bạn
        api.getSummary(studentId, 1L).enqueue(new Callback<StudentSummaryDTO>() {
            @Override
            public void onResponse(Call<StudentSummaryDTO> call, Response<StudentSummaryDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StudentSummaryDTO s = response.body();
                    tvDRL.setText(String.valueOf(s.drl));
                    tvCTXH.setText(String.valueOf(s.ctxh));
                    tvCDDN.setText(String.valueOf(s.cddn));
                } else {
                    tvDRL.setText("0");
                    tvCTXH.setText("0");
                    tvCDDN.setText("0");
                }
            }

            @Override
            public void onFailure(Call<StudentSummaryDTO> call, Throwable t) {
                tvDRL.setText("0");
                tvCTXH.setText("0");
                tvCDDN.setText("0");
            }
        });
    }
}
