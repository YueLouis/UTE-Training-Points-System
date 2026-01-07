package vn.hcmute.utetrainingpointssystem.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.TokenManager;
import vn.hcmute.utetrainingpointssystem.model.auth.AuthResponse;
import vn.hcmute.utetrainingpointssystem.model.auth.VerifySecretRequest;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.AuthApi;
import vn.hcmute.utetrainingpointssystem.ui.admin.AdminDashboardActivity;
import vn.hcmute.utetrainingpointssystem.ui.event.EventListActivity;

public class ForgotCodeActivity extends AppCompatActivity {

    private String email;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_code);

        tokenManager = new TokenManager(this);

        email = getIntent().getStringExtra("email");
        if (email == null) email = "";

        TextView tvEmail = findViewById(R.id.tvEmail);
        tvEmail.setText("Email: " + email);

        TextView tvDemoCode = findViewById(R.id.tvDemoCode);
        String demoCode = getIntent().getStringExtra("demoCode");
        if (demoCode != null && !demoCode.isEmpty()) {
            tvDemoCode.setVisibility(View.VISIBLE);
            tvDemoCode.setText("Mã OTP (Demo): " + demoCode);
        }

        EditText edtCode = findViewById(R.id.edtCode);
        Button btnVerify = findViewById(R.id.btnVerify);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        btnVerify.setOnClickListener(v -> {
            String code = edtCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã", Toast.LENGTH_SHORT).show();
                return;
            }

            btnVerify.setEnabled(false);
            btnVerify.setText("Đang xác thực...");
            progressBar.setVisibility(View.VISIBLE);

            AuthApi api = RetrofitClient.getClient().create(AuthApi.class);
            api.verify(new VerifySecretRequest(email, code)).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    btnVerify.setEnabled(true);
                    btnVerify.setText("Xác nhận");
                    progressBar.setVisibility(View.GONE);

                    if (!response.isSuccessful() || response.body() == null) {
                        String errorMsg = "Xác thực thất bại: " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                String errorJson = response.errorBody().string();
                                if (errorJson.contains("\"message\":\"")) {
                                    int start = errorJson.indexOf("\"message\":\"") + 11;
                                    int end = errorJson.indexOf("\"", start);
                                    errorMsg = errorJson.substring(start, end);
                                }
                            }
                        } catch (Exception e) {
                            // ignore
                        }
                        Toast.makeText(ForgotCodeActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        return;
                    }

                    AuthResponse res = response.body();
                    if (res.user == null) {
                        Toast.makeText(ForgotCodeActivity.this, "Thiếu dữ liệu user", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Sau khi verify thành công, quay lại trang login để user đăng nhập thật
                    Toast.makeText(ForgotCodeActivity.this, "Xác thực thành công! Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ForgotCodeActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    btnVerify.setEnabled(true);
                    btnVerify.setText("Xác nhận");
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgotCodeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
