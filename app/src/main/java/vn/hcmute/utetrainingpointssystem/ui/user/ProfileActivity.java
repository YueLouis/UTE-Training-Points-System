package vn.hcmute.utetrainingpointssystem.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
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

public class ProfileActivity extends AppCompatActivity {

    private TokenManager tokenManager;

    private ImageView btnBack;
    private TextView tvFullName, tvStudentCode, tvEmail, tvPhone, tvClassName;
    private Button btnLogout;

    // ✅ 3 loại điểm
    private TextView tvDRL, tvCTXH, tvCDDN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tokenManager = new TokenManager(this);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tvFullName = findViewById(R.id.tvFullName);
        tvStudentCode = findViewById(R.id.tvStudentCode);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvClassName = findViewById(R.id.tvClassName);

        tvDRL = findViewById(R.id.tvDRL);
        tvCTXH = findViewById(R.id.tvCTXH);
        tvCDDN = findViewById(R.id.tvCDDN);

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            tokenManager.clear();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadUser();
        loadPoints();
    }

    private void loadUser() {
        Long userId = tokenManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "No userId, please login again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        UserApi api = RetrofitClient.getClient().create(UserApi.class);
        api.getUserById(userId).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ProfileActivity.this, "Load user failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                UserDTO u = response.body();

                // ✅ tuỳ field DTO của em
                tvFullName.setText(u.fullName != null ? u.fullName : "");
                tvStudentCode.setText(u.studentCode != null ? u.studentCode : "");
                tvEmail.setText(u.email != null ? u.email : "");
                tvPhone.setText(u.phone != null ? u.phone : "");
                tvClassName.setText(u.className != null ? u.className : "");
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPoints() {
        Long studentId = tokenManager.getStudentId();
        if (studentId == null) return;

        PointApi api = RetrofitClient.getClient().create(PointApi.class);
        // Truyền thêm semesterId = 1L (mặc định) để khớp với API mới
        api.getSummary(studentId, 1L).enqueue(new Callback<StudentSummaryDTO>() {
            @Override
            public void onResponse(Call<StudentSummaryDTO> call, Response<StudentSummaryDTO> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    tvDRL.setText("--");
                    tvCTXH.setText("--");
                    tvCDDN.setText("--");
                    return;
                }

                StudentSummaryDTO s = response.body();

                // ✅ CHỖ NÀY PHẢI KHỚP FIELD BACKEND CỦA EM (Swagger)
                // Nếu backend trả: drl, ctxh, cddn
                tvDRL.setText(String.valueOf(s.drl));
                tvCTXH.setText(String.valueOf(s.ctxh));
                tvCDDN.setText(String.valueOf(s.cddn));
            }

            @Override
            public void onFailure(Call<StudentSummaryDTO> call, Throwable t) {
                tvDRL.setText("--");
                tvCTXH.setText("--");
                tvCDDN.setText("--");
            }
        });
    }
}
